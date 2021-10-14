package com.mattae.simal.modules.base.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.function.EntityResponse;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RouterFunctions.route;

//@Controller
@Slf4j
public class ClientForwardController {

    /**
     * Forwards any unmapped paths (except those containing a period) to the client {@code index.html}.
     *
     * @return forward to client {@code index.html}.
     */
    @GetMapping(value = {"/{path:[^\\.]*}", "/{path:^(?!websocket).*}/**/{path:[^\\.]*}"})
    public String forward() {
        return "forward:/index.html";
    }

    /*@Bean
    public RouterFunction<ServerResponse> forwardToIndex() {
        return route().GET("/graphql", req -> EntityResponse.fromObject("forward:/index.html").build()).build();
    }*/
}
