package com.mattae.simal.modules.base.config;

import com.mattae.simal.modules.base.domain.BaseDomain;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = BaseDomain.class)
public class DomainConfiguration {
}
