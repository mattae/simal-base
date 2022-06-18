package com.mattae.simal.modules.base.graphql.config;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.integration.jackson.EntityViewAwareObjectMapper;
import com.blazebit.persistence.integration.jackson.EntityViewIdValueAccessor;
import com.blazebit.persistence.integration.view.spring.EnableEntityViews;
import com.blazebit.persistence.integration.view.spring.impl.AnnotationEntityViewConfigurationSource;
import com.blazebit.persistence.integration.view.spring.impl.EntityViewConfigurationDelegate;
import com.blazebit.persistence.integration.view.spring.impl.EntityViewConfigurationProducer;
import com.blazebit.persistence.view.ConfigurationProperties;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.EntityViewManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.annotations.Exposed;
import com.mattae.simal.modules.base.config.AuditViewListenersConfiguration;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Configuration
@Exposed
@RequiredArgsConstructor
public class EntityViewConfiguration {

    private final AcrossContext context;
    private final ResourceLoader resourceLoader;
    private final Environment environment;
    private final PlatformTransactionManager tm;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Lazy(false)
    @Primary
    @Exposed
    public EntityViewManager entityViewManager(CriteriaBuilderFactory cbf) throws CannotCompileException {
        com.blazebit.persistence.view.spi.EntityViewConfiguration entityViewConfiguration = configureEntityViews();
        entityViewConfiguration.setProperty(ConfigurationProperties.UPDATER_STRICT_CASCADING_CHECK, "false");
        entityViewConfiguration.setProperty(ConfigurationProperties.UPDATER_FLUSH_MODE, "partial");
        entityViewConfiguration.addEntityViewListener(AuditViewListenersConfiguration.class);
        entityViewConfiguration.setTypeTestValue(UUID.class, UUID.randomUUID());
        return entityViewConfiguration.createEntityViewManager(cbf);
    }

    @Bean
    @Primary
    @Exposed
    public EntityViewAwareObjectMapper getEntityViewAwareObjectMapper(EntityViewIdValueAccessor entityViewIdValueAccessor,
                                                                      EntityViewManager evm, ObjectMapper objectMapper) {
        return new EntityViewAwareObjectMapper(evm, objectMapper, entityViewIdValueAccessor);
    }

    private com.blazebit.persistence.view.spi.EntityViewConfiguration configureEntityViews() throws CannotCompileException {
        List<String> packages = new ArrayList<>();
        context.getModules().forEach(acrossModule -> {
            packages.add(acrossModule.getClass().getPackage().getName());
        });
        Class<?> cls;
        try {
            ClassFile cf = new ClassFile(
                false, "com.mattae.simal.modules.base.graphql.config.EnableEntityViewsConfiguration", null);

            ClassPool classPool = ClassPool.getDefault();
            ConstPool constpool = cf.getConstPool();

            AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
            Annotation annotation = new Annotation(EnableEntityViews.class.getName(), constpool);
            ArrayMemberValue amv = new ArrayMemberValue(new StringMemberValue(constpool), cf.getConstPool());
            List<StringMemberValue> mvs = new ArrayList<>();
            for (String pkg : packages) {
                mvs.add(new StringMemberValue(pkg, constpool));
            }
            amv.setValue(mvs.toArray(new StringMemberValue[0]));
            annotation.addMemberValue("basePackages", amv);
            Annotation config = new Annotation(Configuration.class.getName(), constpool);
            annotationsAttribute.setAnnotations(new Annotation[]{annotation, config});

            cf.addAttribute(annotationsAttribute);
            CtClass ctClass = classPool.makeClass(cf);
            CtConstructor defaultConstructor = CtNewConstructor.make("public " + ctClass.getSimpleName() + "() {}", ctClass);
            ctClass.addConstructor(defaultConstructor);

            cls = classPool.makeClass(cf).toClass(GraphRuntimeWiringConfigurer.class);
        } catch (RuntimeException e) {
            cls = com.mattae.simal.modules.base.config.DomainConfiguration.class;
        }

        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context.getParentApplicationContext()
            .getAutowireCapableBeanFactory();

        AnnotationEntityViewConfigurationSource configurationSource = new AnnotationEntityViewConfigurationSource(
            AnnotationMetadata.introspect(cls), EnableEntityViews.class, resourceLoader, environment);

        EntityViewConfigurationDelegate delegate = new EntityViewConfigurationDelegate(configurationSource, resourceLoader, environment);

        Arrays.stream(delegate.getClass().getDeclaredMethods()).forEach(method -> {
            if (Objects.equals(method.getName(), "registerEntityViews")) {
                method.setAccessible(true);
                try {
                    method.invoke(delegate, registry);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

        Set<Class<?>> entityViewClasses = new HashSet<>();
        Set<Class<?>> entityViewListenerClasses = new HashSet<>();
        for (BeanDefinition candidate : configurationSource.getCandidates(resourceLoader)) {
            try {
                Class<?> clazz = ClassUtils.forName(Objects.requireNonNull(candidate.getBeanClassName()), resourceLoader.getClassLoader());
                if (clazz.isAnnotationPresent(EntityView.class)) {
                    entityViewClasses.add(clazz);
                } else {
                    entityViewListenerClasses.add(clazz);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        EntityViewConfigurationProducer configurationProducer = new EntityViewConfigurationProducer(entityViewClasses, entityViewListenerClasses);
        configurationProducer.setTm(tm);
        return configurationProducer.getEntityViewConfiguration();
    }
}
