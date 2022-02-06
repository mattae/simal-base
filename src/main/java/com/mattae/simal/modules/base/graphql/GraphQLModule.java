package com.mattae.simal.modules.base.graphql;

import com.blazebit.persistence.view.EntityViewManager;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossRole;
import com.foreach.across.core.context.AcrossModuleRole;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfig;
import com.foreach.across.core.context.bootstrap.ModuleBootstrapConfig;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;

@AcrossRole(AcrossModuleRole.POSTPROCESSOR)
public class GraphQLModule extends AcrossModule {
    public static final String NAME = "GraphQLModule";

    public GraphQLModule() {
        super();
        addApplicationContextConfigurer(
            new ComponentScanConfigurer("com.blazebit.persistence.spring.data.webmvc")
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void prepareForBootstrap(ModuleBootstrapConfig currentModule, AcrossBootstrapConfig contextConfig) {
        currentModule.expose("graphQlRouterFunction");
        currentModule.expose(EntityViewManager.class);
    }
}
