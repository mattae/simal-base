package com.mattae.simal.modules.base.security;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.spring.security.configuration.AcrossWebSecurityConfigurer;
import com.mattae.simal.modules.base.security.jwt.JWTConfigurer;
import com.mattae.simal.modules.base.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@Import({SecurityProblemSupport.class})
public class SecurityConfiguration implements AcrossWebSecurityConfigurer {
    private final TokenProvider tokenProvider;
    private final SecurityProblemSupport problemSupport;

    @Bean
    @Exposed
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .antMatcher("/**")
                .csrf()
                    .disable()
                .exceptionHandling()
            .and()
                .headers()
                    .contentSecurityPolicy("default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:")
                .and()
                    .contentSecurityPolicy("font-src 'self' https://fonts.gstatic.com https://fonts.googleapis.com/ 'unsafe-inline'")
                .and()
                    .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                .and()
                    .featurePolicy("geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; speaker 'none'; fullscreen 'self'; payment 'none'")
                .and()
                    .frameOptions()
                .deny()
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .httpBasic()
            .and()
                .apply(securityConfigurerAdapter());
        // @formatter:on
    }

    @Bean
    @Exposed
    public JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }
}
