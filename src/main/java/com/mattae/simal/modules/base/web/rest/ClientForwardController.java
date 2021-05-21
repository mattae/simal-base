package com.mattae.simal.modules.base.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
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
}
