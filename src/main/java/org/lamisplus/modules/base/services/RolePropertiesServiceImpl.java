package org.lamisplus.modules.base.services;

import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.services.AbstractEntityPropertiesService;
import com.foreach.common.spring.properties.PropertyTypeRegistry;
import org.lamisplus.modules.base.business.RoleProperties;
import org.lamisplus.modules.base.business.RolePropertiesService;
import org.lamisplus.modules.base.domain.repositories.RolePropertiesRepository;
import org.springframework.stereotype.Service;

@Service
public class RolePropertiesServiceImpl extends AbstractEntityPropertiesService<RoleProperties, Long> implements RolePropertiesService {
    public RolePropertiesServiceImpl(RolePropertiesRegistry entityPropertiesRegistry,
                                     RolePropertiesRepository entityPropertiesRepository) {
        super(entityPropertiesRegistry, entityPropertiesRepository);
    }

    @Override
    protected RoleProperties createEntityProperties(Long entityId,
                                                    PropertyTypeRegistry<String> propertyTypeRegistry,
                                                    StringPropertiesSource source) {
        return new RoleProperties(entityId, propertyTypeRegistry, source);
    }
}

