package com.mattae.simal.modules.base.graphql;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

@Component
public class QueryResolver implements GraphQLQueryResolver {

    public Stub getStub(Long id) {
        return new Stub(id);
    }
}
