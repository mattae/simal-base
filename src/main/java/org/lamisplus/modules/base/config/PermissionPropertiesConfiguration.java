package org.lamisplus.modules.base.config;

import com.foreach.across.modules.properties.config.AbstractEntityPropertiesConfiguration;
import com.foreach.across.modules.user.business.Permission;
import org.lamisplus.modules.base.BaseModule;
import org.lamisplus.modules.base.business.PermissionPropertiesService;
import org.lamisplus.modules.base.domain.repositories.PermissionPropertiesRepository;
import org.lamisplus.modules.base.services.PermissionPropertiesRegistry;
import org.lamisplus.modules.base.services.PermissionPropertiesServiceImpl;
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

