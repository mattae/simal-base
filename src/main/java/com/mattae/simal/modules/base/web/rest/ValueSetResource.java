package com.mattae.simal.modules.base.web.rest;

import com.mattae.simal.modules.base.domain.entities.ValueSet;
import com.mattae.simal.modules.base.services.ValueSetService;
import io.github.jhipster.web.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Secured("ROLE_USER")
@RestController
@RequestMapping("/api/value-sets")
@RequiredArgsConstructor
public class ValueSetResource {
    private final ValueSetService valueSetService;

    @GetMapping("/provider/{provider}/type/{type}")
    public List<ValueSet.BaseView> getValuesFor(@PathVariable String provider, @PathVariable String type,
                                                @RequestParam(required = false, defaultValue = "true") Boolean active) {
        return valueSetService.getValues(type, provider, active);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ValueSet.BaseView> getById(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(valueSetService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ValueSet.UpdateView> createValue(@RequestBody ValueSet.UpdateView value) {
        value = valueSetService.saveValue(value);
        return ResponseEntity.ok(value);
    }

    @PostMapping("/multi")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ValueSet.UpdateView>> createValues(@RequestBody List<ValueSet.UpdateView> values) {
        values = valueSetService.saveValues(values);
        return ResponseEntity.ok(values);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ValueSet.UpdateView> updateValue(@RequestBody ValueSet.UpdateView value) {
        return createValue(value);
    }
}
