package com.mattae.simal.modules.base.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.modules.filemanager.services.ExpiringFileRepository;
import com.foreach.across.modules.filemanager.services.FileRepository;
import com.foreach.across.modules.filemanager.services.LocalFileRepository;
import com.foreach.across.modules.user.services.UserPropertiesRegistry;
import com.mattae.simal.modules.base.services.PermissionPropertiesRegistry;
import com.mattae.simal.modules.base.services.RolePropertiesRegistry;
import io.github.jhipster.config.JHipsterProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.TypeDescriptor;

import java.time.Instant;
import java.util.UUID;

@Configuration
@Import({
    JHipsterProperties.class,
    JooqAutoConfiguration.class,
    MailSenderAutoConfiguration.class
})
public class ApplicationConfiguration {
    public static final String TEMP_MODULE_DIR = "temp-module-data";

    @Autowired
    public void registerCustomProperties(UserPropertiesRegistry userPropertiesRegistry, RolePropertiesRegistry rolePropertiesRegistry,
                                         PermissionPropertiesRegistry permissionPropertiesRegistry, AcrossModule currentModule) {
        userPropertiesRegistry.register(currentModule, "avatar", TypeDescriptor.valueOf(String.class));
        userPropertiesRegistry.register(currentModule, "activationKey", TypeDescriptor.valueOf(String.class));
        userPropertiesRegistry.register(currentModule, "resetKey", TypeDescriptor.valueOf(String.class));
        userPropertiesRegistry.register(currentModule, "resetDate", TypeDescriptor.valueOf(Instant.class));
        userPropertiesRegistry.register(currentModule, "organisationId", TypeDescriptor.valueOf(UUID.class));
        rolePropertiesRegistry.register(currentModule, "moduleId", TypeDescriptor.valueOf(Long.class));
        permissionPropertiesRegistry.register(currentModule, "moduleId", TypeDescriptor.valueOf(Long.class));
    }

    @Bean
    FileRepository moduleRepository() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        FileRepository repository = LocalFileRepository.builder()
            .repositoryId(TEMP_MODULE_DIR)
            .rootFolder(tmpDir)
            .build();

        return ExpiringFileRepository.builder()
            .targetFileRepository(repository)
            .expireOnShutdown(true)
            .expireOnEvict(false)
            .maxItemsToTrack(50)
            .timeBasedExpiration(60 * 60000L, 24 * 60 * 60000L)
            .build();
    }
}
