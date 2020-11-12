package org.lamisplus.modules.base.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;
import org.springframework.core.annotation.Order;

@Order(2)
@Installer(name = "schema-installer-update", description = "Installs the required database tables", version = 3)
public class SchemaInstallerUpdate extends AcrossLiquibaseInstaller {
    public SchemaInstallerUpdate() {
        super("classpath:installers/lamis-base/schema/update_schema.xml");
    }
}
