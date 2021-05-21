package com.mattae.simal.modules.base.graphql;

import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Component;

@Component
public class MutationResolver implements GraphQLMutationResolver {

    public Stub saveStub(Long id) {
        return new Stub(id);
    }
}
