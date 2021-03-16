package org.lamisplus.modules.base.services;

import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import org.springframework.stereotype.Service;

@Service
public class RolePropertiesRegistry extends EntityPropertiesRegistry {
    public RolePropertiesRegistry(EntityPropertiesDescriptor descriptor) {
        super(descriptor);
    }
}
