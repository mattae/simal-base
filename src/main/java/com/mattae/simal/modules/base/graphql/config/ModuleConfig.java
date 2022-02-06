package com.mattae.simal.modules.base.graphql.config;

import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.modules.web.AcrossWebModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.GraphQlService;
import org.springframework.graphql.boot.GraphQlAutoConfiguration;
import org.springframework.graphql.boot.GraphQlServiceAutoConfiguration;
import org.springframework.graphql.boot.GraphQlWebMvcAutoConfiguration;
import org.springframework.graphql.execution.ThreadLocalAccessor;
import org.springframework.graphql.web.WebGraphQlHandler;
import org.springframework.graphql.web.WebInterceptor;

import java.util.stream.Collectors;

@ModuleConfiguration(AcrossWebModule.NAME)
@Import({
    GraphQlAutoConfiguration.class,
    GraphQlWebMvcAutoConfiguration.class,
    GraphQlServiceAutoConfiguration.class
})
@RequiredArgsConstructor
public class ModuleConfig {

    @Bean
    @ConditionalOnBean(GraphQlService.class)
    @ConditionalOnMissingBean
    public WebGraphQlHandler webGraphQlHandler(GraphQlService service, ObjectProvider<WebInterceptor> interceptorsProvider,
                                               ObjectProvider<ThreadLocalAccessor> accessorsProvider) {
        return WebGraphQlHandler.builder(service)
            .interceptors(interceptorsProvider.orderedStream().collect(Collectors.toList()))
            .threadLocalAccessors(accessorsProvider.orderedStream().collect(Collectors.toList())).build();
    }
}
