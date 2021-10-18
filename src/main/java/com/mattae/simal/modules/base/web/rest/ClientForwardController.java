package com.mattae.simal.modules.base.web.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.function.EntityResponse;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@Controller
public class ClientForwardController {

    /**
     * Forwards any unmapped paths (except those containing a period) to the client {@code index.html}.
     *
     * @return forward to client {@code index.html}.
     */
    @Bean
    @Order
    public RouterFunction<ServerResponse> forwardToIndex() {
        return route()
            .GET("/{path:[^\\.]*}", req -> EntityResponse.fromObject("forward:/index.html").build())
            .build();
    }
}
