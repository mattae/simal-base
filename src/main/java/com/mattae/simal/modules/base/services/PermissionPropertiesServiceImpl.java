package com.mattae.simal.modules.base.services;

import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.services.AbstractEntityPropertiesService;
import com.foreach.common.spring.properties.PropertyTypeRegistry;
import com.mattae.simal.modules.base.business.PermissionProperties;
import com.mattae.simal.modules.base.business.PermissionPropertiesService;
import com.mattae.simal.modules.base.domain.repositories.PermissionPropertiesRepository;
import org.springframework.stereotype.Service;

@Service
public class PermissionPropertiesServiceImpl extends AbstractEntityPropertiesService<PermissionProperties, Long> implements PermissionPropertiesService {
    public PermissionPropertiesServiceImpl(PermissionPropertiesRegistry entityPropertiesRegistry,
                                           PermissionPropertiesRepository entityPropertiesRepository) {
        super(entityPropertiesRegistry, entityPropertiesRepository);
    }

    @Override
    protected PermissionProperties createEntityProperties(Long entityId,
                                                          PropertyTypeRegistry<String> propertyTypeRegistry,
                                                          StringPropertiesSource source) {
        return new PermissionProperties(entityId, propertyTypeRegistry, source);
    }
}

