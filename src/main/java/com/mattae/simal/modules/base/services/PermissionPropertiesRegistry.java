package com.mattae.simal.modules.base.services;

import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import org.springframework.stereotype.Service;

@Service
public class PermissionPropertiesRegistry extends EntityPropertiesRegistry {
    public PermissionPropertiesRegistry(EntityPropertiesDescriptor descriptor) {
        super(descriptor);
    }
}
