package org.lamisplus.modules.base.config;

import com.foreach.across.modules.properties.config.AbstractEntityPropertiesConfiguration;
import org.lamisplus.modules.base.BaseModule;
import org.lamisplus.modules.base.business.RolePropertiesService;
import org.lamisplus.modules.base.domain.repositories.RolePropertiesRepository;
import org.lamisplus.modules.base.services.RolePropertiesRegistry;
import org.lamisplus.modules.base.services.RolePropertiesServiceImpl;
import org.lamisplus.modules.base.yml.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RolePropertiesConfiguration extends AbstractEntityPropertiesConfiguration {
    public static final String ID = BaseModule.NAME + ".RoleProperties";

    @Override
    public Class<?> entityClass() {
        return Role.class;
    }

    @Override
    public String propertiesId() {
        return ID;
    }

    @Override
    protected String originalTableName() {
        return "um_role_properties";
    }

    @Override
    public String keyColumnName() {
        return "role_id";
    }

    @Bean(name = "rolePropertiesService")
    @Override
    public RolePropertiesService service() {
        return new RolePropertiesServiceImpl(registry(), rolePropertiesRepository());
    }

    @Bean
    public RolePropertiesRepository rolePropertiesRepository() {
        return new RolePropertiesRepository(this);
    }

    @Bean(name = "rolePropertiesRegistry")
    @Override
    public RolePropertiesRegistry registry() {
        return new RolePropertiesRegistry(this);
    }
}
