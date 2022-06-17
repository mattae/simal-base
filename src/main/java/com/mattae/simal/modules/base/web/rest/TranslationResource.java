package com.mattae.simal.modules.base.web.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.mattae.simal.modules.base.domain.entities.Translation;
import com.mattae.simal.modules.base.services.TranslationService;
import com.mattae.simal.modules.base.web.errors.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/translations")
@RequiredArgsConstructor
public class TranslationResource {
    private final TranslationService translationService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Translation save(@Valid @RequestBody Translation translation) {
        if (translation.getId() != null) {
            throw new BadRequestException("New translation cannot have ID");
        }
        return translationService.save(translation);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Translation update(@Valid @RequestBody Translation translation) {
        if (translation.getId() == null) {
            throw new BadRequestException("Updating translation requires an ID");
        }
        return translationService.save(translation);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Optional<Translation> getById(@PathVariable Long id) {
        return translationService.getById(id);
    }

    @GetMapping("/lang/{lang}")
    public JsonNode getTranslationsForLang(@PathVariable String lang) {
        return translationService.listByLang(lang);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteById(@PathVariable Long id) {
        translationService.deleteById(id);
    }
}

