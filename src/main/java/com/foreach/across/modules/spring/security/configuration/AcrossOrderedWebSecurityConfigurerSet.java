/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.spring.security.configuration;

import com.foreach.across.core.context.AcrossListableBeanFactory;
import com.foreach.across.core.context.AcrossOrderSpecifierComparator;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.core.context.support.AcrossOrderSpecifier;
import javassist.ClassPool;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;

import java.util.*;

/**
 * Any {@link AcrossWebSecurityConfigurer} will be wrapped into a {@link AcrossWebSecurityConfigurerAdapter} which will
 * be created entirely inside this module. Any {@link WebSecurityConfigurer} will be wrapped inside a
 *
 * @author Arne Vandamme
 * @since 4.0.0
 */
class AcrossOrderedWebSecurityConfigurerSet extends LinkedHashSet<WebSecurityConfigurer<?>> {
    private final AcrossListableBeanFactory beanFactory;

    AcrossOrderedWebSecurityConfigurerSet(AcrossModuleInfo currentModule) {
        beanFactory = (AcrossListableBeanFactory) currentModule.getApplicationContext().getAutowireCapableBeanFactory();

        AcrossOrderSpecifierComparator comparator = new AcrossOrderSpecifierComparator();
        Set<Object> configurers = new LinkedHashSet<>();

        addWebSecurityConfigurers(WebSecurityConfigurer.class, configurers, comparator);
        addWebSecurityConfigurers(AcrossWebSecurityConfigurer.class, configurers, comparator);

        List<Object> sortedList = new ArrayList<>(configurers);
        sortedList.sort(comparator);

        for (int i = 0; i < sortedList.size(); i++) {
            add(createWrapperOrProxy(sortedList.get(i), i));
        }
    }

    private WebSecurityConfigurer<?> createWrapperOrProxy(Object original, int order) {
        if (original instanceof AcrossWebSecurityConfigurer) {
            return createWrapper((AcrossWebSecurityConfigurer) original, order);
        }
        return createProxy((WebSecurityConfigurer<?>) original, order);
    }

    @SneakyThrows
    private WebSecurityConfigurerProxy<?> createProxy(WebSecurityConfigurer<?> configurer, int order) {
        ClassPool pool = ClassPool.getDefault();
        Class<?> dynamicInterface = pool.makeInterface(AcrossOrderedWebSecurityConfigurerSet.class.getPackageName() + "." +
            RandomStringUtils.randomAlphabetic(10)).toClass(AcrossOrderedWebSecurityConfigurerSet.class);

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(WebSecurityConfigurerProxy.class);
        enhancer.setInterfaces(new Class[]{dynamicInterface});
        enhancer.setCallback(NoOp.INSTANCE);

        return (WebSecurityConfigurerProxy<?>) enhancer.create(
            new Class[]{WebSecurityConfigurer.class, int.class},
            new Object[]{configurer, order}
        );
    }

    /**
     * Creates a dynamic wrapper for a AcrossWebSecurityConfigurer.  Due to the standard configuration of http
     * security, the wrapper MUST be a different class each time.  To achieve that, a dynamically created interface
     * is added to every wrapper.
     */
    @SneakyThrows
    private AcrossWebSecurityConfigurerAdapter createWrapper(AcrossWebSecurityConfigurer configurer, int order) {
        ClassPool pool = ClassPool.getDefault();
        Class<?> dynamicInterface = pool.makeInterface(AcrossOrderedWebSecurityConfigurerSet.class.getPackageName() + "." +
            RandomStringUtils.randomAlphabetic(10)).toClass(AcrossOrderedWebSecurityConfigurerSet.class);

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(AcrossWebSecurityConfigurerAdapter.class);
        enhancer.setInterfaces(new Class[]{dynamicInterface});
        enhancer.setCallback(NoOp.INSTANCE);

        AcrossWebSecurityConfigurerAdapter wrapper = (AcrossWebSecurityConfigurerAdapter) enhancer.create(
            new Class[]{AcrossWebSecurityConfigurer.class, int.class},
            new Object[]{configurer, order}
        );

        beanFactory.autowireBean(wrapper);

        return wrapper;
    }

    private void addWebSecurityConfigurers(Class<?> configurerType, Set<Object> list, AcrossOrderSpecifierComparator comparator) {
        Map<String, ?> configurers = beanFactory.getBeansOfType(configurerType);

        configurers.forEach((beanName, configurer) -> {
            comparator.register(configurer, beanFactory.retrieveOrderSpecifier(beanName));
            list.add(configurer);
        });

        ListableBeanFactory pbf = (ListableBeanFactory) beanFactory.getParentBeanFactory();

        if (pbf != null) {
            BeanFactoryUtils.beansOfTypeIncludingAncestors(pbf, configurerType)
                .forEach((beanName, configurer) -> {
                    if (!list.contains(configurer)) {
                        comparator.register(configurer, AcrossOrderSpecifier.forSources(Collections.singletonList(configurer)).build());
                        list.add(configurer);
                    }
                });
        }
    }

    public List<WebSecurityConfigurer<?>> getWebSecurityConfigurers() {
        return new ArrayList<>(this);
    }
}
