package com.mattae.simal.modules.base.config;

import com.foreach.across.modules.filemanager.services.ExpiringFileRepository;
import com.foreach.across.modules.filemanager.services.FileRepository;
import com.foreach.across.modules.filemanager.services.LocalFileRepository;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    JooqAutoConfiguration.class,
    MailSenderAutoConfiguration.class
})
public class ApplicationConfiguration {
    public static final String TEMP_MODULE_DIR = "temp-module-data";

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
