package com.mattae.simal.modules.base.config;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import java.util.List;

@Configuration
public class JacksoMessageConverterConfigurer implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
            .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
            .forEach(converter -> ((MappingJackson2HttpMessageConverter) converter).getObjectMapper()
                .registerModules(
                    new ProblemModule(),
                    new Jdk8Module(),
                    new JavaTimeModule(),
                    new Hibernate5Module(),
                    new AfterburnerModule(),
                    new ConstraintViolationProblemModule()
                ));
    }
}
