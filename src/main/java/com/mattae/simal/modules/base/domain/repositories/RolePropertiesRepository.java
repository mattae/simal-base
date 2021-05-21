package com.mattae.simal.modules.base.domain.repositories;

import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;

public class RolePropertiesRepository extends EntityPropertiesRepository<Long> {
    public RolePropertiesRepository(EntityPropertiesDescriptor configuration) {
        super(configuration);
    }
}
