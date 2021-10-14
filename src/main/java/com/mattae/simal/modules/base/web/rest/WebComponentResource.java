package com.mattae.simal.modules.base.web.rest;

import com.mattae.simal.modules.base.services.WebComponentService;
import com.mattae.simal.modules.base.services.dto.ComponentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing registered Angular Components.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class WebComponentResource {
    private final WebComponentService webComponentService;

    /**
     * GET  /web-components/{id} : get Web Component by id.
     *
     * @return the ResponseEntity with components 200 (OK) and the web component in body
     */
    @GetMapping("/web-components/{id}")
    public ComponentDTO getComponentById(@PathVariable UUID id) {
        LOG.debug("REST request to get Web Component with id: {}", id);
        return webComponentService.getComponentById(id);
    }

    /**
     * GET  /web-components/{type} : get all the active components by type.
     *
     * @return the ResponseEntity with components 200 (OK) and the list of components in body
     */
    @GetMapping("/web-components/{type}")
    public List<ComponentDTO> getAllActiveWebComponents(@PathVariable String type) {
        LOG.debug("REST request to get all Components of type: {}", type);
        return webComponentService.getComponentsByType(type);
    }
}
