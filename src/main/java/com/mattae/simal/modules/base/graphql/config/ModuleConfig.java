package com.mattae.simal.modules.base.graphql.config;

import com.blazebit.persistence.integration.view.spring.EnableEntityViews;
import com.blazebit.persistence.integration.view.spring.impl.AnnotationEntityViewConfigurationSource;
import com.blazebit.persistence.integration.view.spring.impl.EntityViewConfigurationDelegate;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.modules.web.AcrossWebModule;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.graphql.GraphQlService;
import org.springframework.graphql.boot.GraphQlAutoConfiguration;
import org.springframework.graphql.boot.GraphQlServiceAutoConfiguration;
import org.springframework.graphql.boot.GraphQlWebMvcAutoConfiguration;
import org.springframework.graphql.execution.ThreadLocalAccessor;
import org.springframework.graphql.web.WebGraphQlHandler;
import org.springframework.graphql.web.WebInterceptor;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ModuleConfiguration(AcrossWebModule.NAME)
@Import({
    GraphQlAutoConfiguration.class,
    GraphQlWebMvcAutoConfiguration.class,
    GraphQlServiceAutoConfiguration.class
})
@RequiredArgsConstructor
@Slf4j
public class ModuleConfig {
    private final AcrossContext context;
    private final ResourceLoader resourceLoader;
    private final Environment environment;

    @Bean
    @ConditionalOnBean(GraphQlService.class)
    @ConditionalOnMissingBean
    public WebGraphQlHandler webGraphQlHandler(GraphQlService service, ObjectProvider<WebInterceptor> interceptorsProvider,
                                               ObjectProvider<ThreadLocalAccessor> accessorsProvider) {
        return WebGraphQlHandler.builder(service)
            .interceptors(interceptorsProvider.orderedStream().collect(Collectors.toList()))
            .threadLocalAccessors(accessorsProvider.orderedStream().collect(Collectors.toList())).build();
    }

    public void configureEntityViews() throws CannotCompileException {
        List<String> packages = new ArrayList<>();
        context.getModules().forEach(acrossModule -> {
            packages.add(acrossModule.getClass().getPackage().getName());
        });
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
        Class<?> cls = classPool.makeClass(cf).toClass(GraphRuntimeWiringConfigurer.class);

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
    }

    @PostConstruct
    public void init() {
        try {
            configureEntityViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
