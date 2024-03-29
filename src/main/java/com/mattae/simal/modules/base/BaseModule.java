package com.mattae.simal.modules.base;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;
import com.foreach.across.modules.filemanager.FileManagerModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.across.modules.spring.batch.SpringBatchModule;
import com.foreach.across.modules.web.AcrossWebModule;
import com.mattae.simal.modules.base.graphql.GraphQLModule;

@AcrossDepends(required = {
    AcrossHibernateJpaModule.NAME,
    PropertiesModule.NAME,
    FileManagerModule.NAME,
    SpringBatchModule.NAME,
    GraphQLModule.NAME,
    AcrossWebModule.NAME
})
public class BaseModule extends AcrossModule {
    public static final String NAME = "SIMALCoreModule";

    public BaseModule() {
        super();
        addApplicationContextConfigurer(new ComponentScanConfigurer(getClass().getPackage().getName() + ".services",
            getClass().getPackage().getName() + ".web", getClass().getPackage().getName() + ".security",
            getClass().getPackage().getName() + ".module", "org.springframework.web.socket",
            "com.blazebit.persistence.spring.data.webmvc"
        ));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Module containing SIMAL core functionalities";
    }
}
