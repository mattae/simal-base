package org.lamisplus.modules.base.config;

import com.foreach.across.modules.hibernate.jpa.repositories.config.EnableAcrossJpaRepositories;
import org.lamisplus.modules.base.domain.BaseDomain;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAcrossJpaRepositories(basePackageClasses = BaseDomain.class)
public class DomainConfiguration {
}
