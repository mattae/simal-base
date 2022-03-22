package com.mattae.simal.modules.base.web.rest;

import com.mattae.simal.modules.base.domain.entities.ValueSet;
import com.mattae.simal.modules.base.services.ValueSetService;
import io.github.jhipster.web.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('ROLE_USER')")
@RestController
@RequestMapping("/api/value-sets")
@RequiredArgsConstructor
public class ValueSetResource {
    public final ValueSetService valueSetService;

    @GetMapping("/provider/{provider}/type/{type}")
    public List<ValueSet.BaseView> getValuesFor(@PathVariable String provider, @PathVariable String type,
                                                @RequestParam(required = false, defaultValue = "true") Boolean active,
                                                @RequestParam(required = false) String lang) {
        return valueSetService.getValues(type, provider, active, lang);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ValueSet.BaseView> getById(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(valueSetService.getById(id));
    }

    @GetMapping("/display-text/type/{type}/provider/{provider}/code/{code}")
    public String getDisplayText(@PathVariable String type, @PathVariable String provider, @PathVariable String code,
                                 @RequestParam(required = false) String lang) {
        return valueSetService.getDisplay(type, provider, lang, code);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ValueSet.UpdateView> createValue(@RequestBody ValueSet.BaseView value) {
        ValueSet.UpdateView result = valueSetService.saveValue(value);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/multi")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ValueSet.UpdateView>> createValues(@RequestBody List<ValueSet.BaseView> values) {
        List<ValueSet.UpdateView> result = valueSetService.saveValues(values);
        return ResponseEntity.ok(result);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ValueSet.UpdateView> updateValue(@RequestBody ValueSet.UpdateView value) {
        return createValue(value);
    }
}
