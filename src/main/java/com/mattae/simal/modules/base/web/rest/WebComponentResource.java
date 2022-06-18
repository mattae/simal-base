package com.mattae.simal.modules.base.web.rest;

import com.mattae.simal.modules.base.domain.entities.ExposedComponent;
import com.mattae.simal.modules.base.services.WebComponentService;
import io.github.jhipster.web.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WebComponentResource {
    private final WebComponentService webComponentService;


    @GetMapping("/web-components/name/{name}")
    public ResponseEntity<ExposedComponent.View> getByName(@PathVariable UUID name) {
        return ResponseUtil.wrapOrNotFound(webComponentService.findByName(name));
    }
}
