package org.lamisplus.modules.base.business;

import com.foreach.across.modules.properties.business.EntityProperties;
import com.foreach.common.spring.properties.PropertiesSource;
import com.foreach.common.spring.properties.PropertyTypeRegistry;

public class PermissionProperties extends EntityProperties<Long> {
    private final long permissionId;

    public PermissionProperties(long permissionId,
                                PropertyTypeRegistry<String> propertyTypeRegistry,
                                PropertiesSource source) {
        super(propertyTypeRegistry, source);

        this.permissionId = permissionId;
    }

    @Override
    public Long getId() {
        return permissionId;
    }
}
