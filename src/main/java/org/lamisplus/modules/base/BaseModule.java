package org.lamisplus.modules.base;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfig;
import com.foreach.across.core.context.bootstrap.ModuleBootstrapConfig;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.web.AcrossWebModule;
import lombok.extern.slf4j.Slf4j;

@AcrossDepends(required = {
    AcrossHibernateJpaModule.NAME,
    UserModule.NAME,
    AcrossWebModule.NAME
})
@Slf4j
public class BaseModule extends AcrossModule {
    public static final String NAME = "LAMISBaseModule";

    public BaseModule() {
        super();
        addApplicationContextConfigurer(new ComponentScanConfigurer(getClass().getPackage().getName() + ".services",
            getClass().getPackage().getName() + ".web", getClass().getPackage().getName() + ".security",
            getClass().getPackage().getName() + ".module", "org.springframework.web.socket",
            getClass().getPackage().getName() + ".module"
        ));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Module containing LAMIS base entities and services";
    }
}
