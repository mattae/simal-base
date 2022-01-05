package com.mattae.simal.modules.base.graphql.config;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.integration.jackson.EntityViewAwareObjectMapper;
import com.blazebit.persistence.integration.jackson.EntityViewIdValueAccessor;
import com.blazebit.persistence.view.ConfigurationProperties;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.spi.EntityViewConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foreach.across.core.annotations.Exposed;
import com.mattae.simal.modules.base.config.AuditViewListenersConfiguration;
import com.mattae.simal.modules.base.config.ContextProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;

import java.util.UUID;

@Configuration
@Exposed
public class DomainConfiguration {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Lazy(false)
    @Primary
    @Exposed
    public EntityViewManager createEntityViewManager(CriteriaBuilderFactory cbf) {
        EntityViewConfiguration entityViewConfiguration = (EntityViewConfiguration) ContextProvider.getBean("getEntityViewConfiguration");
        entityViewConfiguration.setProperty(ConfigurationProperties.UPDATER_STRICT_CASCADING_CHECK, "false");
        entityViewConfiguration.setProperty(ConfigurationProperties.UPDATER_FLUSH_MODE, "partial");
        entityViewConfiguration.addEntityViewListener(AuditViewListenersConfiguration.class);
        entityViewConfiguration.setTypeTestValue(UUID.class, UUID.randomUUID());
        return entityViewConfiguration.createEntityViewManager(cbf);
    }

    @Bean
    @Primary
    @Exposed
    public EntityViewAwareObjectMapper getEntityViewAwareObjectMapper(EntityViewManager evm, ObjectMapper objectMapper,
                                                                      EntityViewIdValueAccessor entityViewIdValueAccessor) {
        return new EntityViewAwareObjectMapper(evm, objectMapper, entityViewIdValueAccessor);
    }
}
