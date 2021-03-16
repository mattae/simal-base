package org.lamisplus.modules.base.business;

import com.foreach.across.modules.properties.business.EntityProperties;
import com.foreach.common.spring.properties.PropertiesSource;
import com.foreach.common.spring.properties.PropertyTypeRegistry;

public class RoleProperties extends EntityProperties<Long> {
    private final long roleId;

    public RoleProperties(long roleId,
                          PropertyTypeRegistry<String> propertyTypeRegistry,
                          PropertiesSource source) {
        super(propertyTypeRegistry, source);

        this.roleId = roleId;
    }

    @Override
    public Long getId() {
        return roleId;
    }
}
