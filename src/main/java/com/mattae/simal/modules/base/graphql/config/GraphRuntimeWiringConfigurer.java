package com.mattae.simal.modules.base.graphql.config;

import graphql.scalars.ExtendedScalars;
import graphql.schema.idl.RuntimeWiring;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphRuntimeWiringConfigurer implements RuntimeWiringConfigurer {
    @Override
    public void configure(RuntimeWiring.Builder builder) {
        builder.scalar(ExtendedScalars.Json)
            .scalar(ExtendedScalars.Time)
            .scalar(ExtendedScalars.DateTime)
            .scalar(ExtendedScalars.Date)
            .scalar(ExtendedScalars.Object);
    }
}
