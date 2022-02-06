package com.mattae.simal.modules.base.config;

import com.foreach.across.core.annotations.Exposed;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "jwt", ignoreUnknownFields = false)
@Configuration
@Exposed
@Getter
@Setter
public class JwtProperties {
    private Long refreshTokenDurationMillis;
    private Long tokenValidityInSecs;
    private Long tokenValidityInSecsForRememberMe;
    private String base64Secret;
}
