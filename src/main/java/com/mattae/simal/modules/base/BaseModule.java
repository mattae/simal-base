package com.mattae.simal.modules.base;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;
import com.foreach.across.modules.filemanager.FileManagerModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.spring.batch.SpringBatchModule;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.web.AcrossWebModule;
import lombok.extern.slf4j.Slf4j;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.boot.autoconfigure.jooq.SpringTransactionProvider;

@AcrossDepends(required = {
    AcrossHibernateJpaModule.NAME,
    FileManagerModule.NAME,
    SpringBatchModule.NAME,
    UserModule.NAME,
    AcrossWebModule.NAME
})
@Slf4j
public class BaseModule extends AcrossModule {
    public static final String NAME = "SIMALBaseModule";

    public BaseModule() {
        super();
        addApplicationContextConfigurer(new ComponentScanConfigurer(getClass().getPackage().getName() + ".services",
            getClass().getPackage().getName() + ".web", getClass().getPackage().getName() + ".security",
            getClass().getPackage().getName() + ".module", "org.springframework.web.socket",
            getClass().getPackage().getName() + ".module",
            getClass().getPackage().getName() + ".graphql"
        ));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Module containing SIMAL base entities and services";
    }
}
