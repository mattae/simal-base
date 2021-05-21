package com.mattae.simal.modules.base.security;

import com.foreach.across.modules.spring.security.configuration.AcrossWebSecurityConfigurer;
import lombok.RequiredArgsConstructor;
import com.mattae.simal.modules.base.security.jwt.JWTConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@RequiredArgsConstructor
public class ApiSecurityConfiguration implements AcrossWebSecurityConfigurer {
    private final JWTConfigurer jwtConfigurer;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
            .disable()
            .requestMatchers()
            .antMatchers("/api/**")
            .and().authorizeRequests().anyRequest().authenticated()
            .and()
            .apply(jwtConfigurer);
    }
}
