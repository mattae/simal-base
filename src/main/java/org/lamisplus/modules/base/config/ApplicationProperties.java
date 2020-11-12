package org.lamisplus.modules.base.config;

import com.foreach.across.core.annotations.Exposed;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "lamis", ignoreUnknownFields = false)
@Configuration
@Exposed
@Getter
@Setter
@EnableAutoConfiguration
public class ApplicationProperties {
    private String modulePath = "modules";
    private String databaseDir;
    private String tempDir;
    private String serverUrl;
}
