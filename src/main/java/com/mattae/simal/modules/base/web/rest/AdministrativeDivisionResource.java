package com.mattae.simal.modules.base.web.rest;

import com.mattae.simal.modules.base.domain.entities.AdministrativeDivision;
import com.mattae.simal.modules.base.domain.repositories.AdministrativeDivisionRepository;
import com.mattae.simal.modules.base.domain.repositories.CountryRepository;
import io.github.jhipster.web.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for interacting with AdministrativeDivisions.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class AdministrativeDivisionResource {
    private final AdministrativeDivisionRepository administrativeDivisionRepository;
    private final CountryRepository countryRepository;

    @GetMapping("/administrative-divisions/country/{countryCode}")
    public List<AdministrativeDivision> getTopLevelOneAdministrativeDivisionsByCountry(@PathVariable String countryCode) {
        return countryRepository.findByCode(countryCode)
            .map(administrativeDivisionRepository::findByCountryAndParentIsNull).orElse(new ArrayList<>());
    }

    @GetMapping("/administrative-divisions/parent/{parentId}")
    public List<AdministrativeDivision> getAdministrativeDivisionsByParent(@PathVariable Long parentId) {
        return administrativeDivisionRepository.findById(parentId)
            .map(administrativeDivisionRepository::findByParent).orElse(new ArrayList<>());
    }
    @GetMapping("/administrative-divisions/{id}")
    public ResponseEntity<AdministrativeDivision> getById(@PathVariable Long id) {
        Optional<AdministrativeDivision> division = administrativeDivisionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(division);
    }
}
