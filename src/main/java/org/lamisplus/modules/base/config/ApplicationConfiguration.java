package org.lamisplus.modules.base.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.modules.user.services.UserPropertiesRegistry;
import org.lamisplus.modules.base.services.PermissionPropertiesRegistry;
import org.lamisplus.modules.base.services.RolePropertiesRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.TypeDescriptor;

import java.time.Instant;

@Configuration
public class ApplicationConfiguration {

    @Autowired
    public void registerCustomProperties(UserPropertiesRegistry userPropertiesRegistry, RolePropertiesRegistry rolePropertiesRegistry,
                                         PermissionPropertiesRegistry permissionPropertiesRegistry, AcrossModule currentModule) {
        userPropertiesRegistry.register(currentModule, "avatar", TypeDescriptor.valueOf(String.class));
        userPropertiesRegistry.register(currentModule, "activationKey", TypeDescriptor.valueOf(String.class));
        userPropertiesRegistry.register(currentModule, "resetKey", TypeDescriptor.valueOf(String.class));
        userPropertiesRegistry.register(currentModule, "resetDate", TypeDescriptor.valueOf(Instant.class));
        rolePropertiesRegistry.register(currentModule, "moduleId", TypeDescriptor.valueOf(Long.class));
        permissionPropertiesRegistry.register(currentModule, "moduleId", TypeDescriptor.valueOf(Long.class));
    }
}
