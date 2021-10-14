package com.mattae.simal.modules.base.config;

import com.blazebit.persistence.integration.view.spring.EnableEntityViews;
import com.blazebit.persistence.spring.data.impl.repository.BlazePersistenceRepositoryFactoryBean;
import com.mattae.simal.modules.base.domain.BaseDomain;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = BaseDomain.class, repositoryFactoryBeanClass = BlazePersistenceRepositoryFactoryBean.class)
@EnableEntityViews(basePackageClasses = BaseDomain.class)
public class DomainConfiguration {
}
