package com.mattae.simal.modules.base.config;

import com.foreach.across.modules.properties.config.AbstractEntityPropertiesConfiguration;
import com.foreach.across.modules.user.business.Permission;
import com.mattae.simal.modules.base.business.PermissionPropertiesService;
import com.mattae.simal.modules.base.domain.repositories.PermissionPropertiesRepository;
import com.mattae.simal.modules.base.services.PermissionPropertiesRegistry;
import com.mattae.simal.modules.base.services.PermissionPropertiesServiceImpl;
import com.mattae.simal.modules.base.BaseModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PermissionPropertiesConfiguration extends AbstractEntityPropertiesConfiguration {
    public static final String ID = BaseModule.NAME + ".PermissionProperties";

    @Override
    public Class<?> entityClass() {
        return Permission.class;
    }

    @Override
    public String propertiesId() {
        return ID;
    }

    @Override
    protected String originalTableName() {
        return "um_permission_properties";
    }

    @Override
    public String keyColumnName() {
        return "permission_id";
    }

    @Bean(name = "permissionPropertiesService")
    @Override
    public PermissionPropertiesService service() {
        return new PermissionPropertiesServiceImpl(registry(), permissionPropertiesRepository());
    }

    @Bean
    public PermissionPropertiesRepository permissionPropertiesRepository() {
        return new PermissionPropertiesRepository(this);
    }

    @Bean(name = "permissionPropertiesRegistry")
    @Override
    public PermissionPropertiesRegistry registry() {
        return new PermissionPropertiesRegistry(this);
    }
}

