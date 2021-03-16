package org.lamisplus.modules.base.config;

import com.foreach.across.core.annotations.Exposed;
import io.github.jhipster.config.JHipsterProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@ConfigurationProperties(prefix = "lamis", ignoreUnknownFields = false)
@Configuration
@Exposed
@Getter
@Setter
@Import({
    JHipsterProperties.class,
    org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration.class
})
public class ApplicationProperties {
    private String modulePath = "modules";
    private String databaseDir;
    private String tempDir;
    private String serverUrl;
}
