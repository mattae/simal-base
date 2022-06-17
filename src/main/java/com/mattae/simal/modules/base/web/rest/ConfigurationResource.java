package com.mattae.simal.modules.base.web.rest;

import com.mattae.simal.modules.base.domain.entities.Configuration;
import com.mattae.simal.modules.base.services.ConfigurationService;
import io.github.jhipster.web.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/base/configurations")
@RequiredArgsConstructor
public class ConfigurationResource {
    private final ConfigurationService configurationService;

    @GetMapping("get-as-string/category/{category}/key/{key}")
    public ResponseEntity<String> getAsString(@PathVariable String category, @PathVariable String key) {
        return ResponseUtil.wrapOrNotFound(configurationService.getValueAsStringForKey(category, key));
    }

    @GetMapping("get-as-date/category/{category}/key/{key}")
    public ResponseEntity<LocalDate> getAsDate(@PathVariable String category, @PathVariable String key) {
        return ResponseUtil.wrapOrNotFound(configurationService.getValueAsDateForKey(category, key));
    }

    @GetMapping("get-as-boolean/category/{category}/key/{key}")
    public ResponseEntity<Boolean> getAsBool(@PathVariable String category, @PathVariable String key) {
        return ResponseUtil.wrapOrNotFound(configurationService.getValueAsBooleanForKey(category, key));
    }

    @GetMapping("get-as-numeric/category/{category}/key/{key}")
    public ResponseEntity<Double> getAsNumeric(@PathVariable String category, @PathVariable String key) {
        return ResponseUtil.wrapOrNotFound(configurationService.getValueAsNumericForKey(category, key));
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Configuration.View updateConfiguration(@Valid @RequestBody Configuration.View configuration,
                                                  @PathVariable Long id) {
        return configurationService.update(configuration);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Configuration.View> list(@RequestParam(required = false) String category,
                                         @RequestParam(required = false) String key) {
        return configurationService.list(category, key);
    }
}
