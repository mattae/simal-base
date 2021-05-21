package com.mattae.simal.modules.base.config;

import com.foreach.across.modules.properties.config.AbstractEntityPropertiesConfiguration;
import com.mattae.simal.modules.base.business.RolePropertiesService;
import com.mattae.simal.modules.base.domain.repositories.RolePropertiesRepository;
import com.mattae.simal.modules.base.services.RolePropertiesRegistry;
import com.mattae.simal.modules.base.services.RolePropertiesServiceImpl;
import com.mattae.simal.modules.base.BaseModule;
import com.mattae.simal.modules.base.yml.Role;
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
