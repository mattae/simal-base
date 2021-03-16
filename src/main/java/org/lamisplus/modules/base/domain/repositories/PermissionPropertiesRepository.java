package org.lamisplus.modules.base.domain.repositories;

import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;

public class PermissionPropertiesRepository extends EntityPropertiesRepository<Long> {
    public PermissionPropertiesRepository(EntityPropertiesDescriptor configuration) {
        super(configuration);
    }
}
