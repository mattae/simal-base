package com.mattae.simal.modules.base.web.rest;

import com.mattae.simal.modules.base.domain.entities.AdministrativeDivision;
import com.mattae.simal.modules.base.domain.repositories.AdministrativeDivisionRepository;
import com.mattae.simal.modules.base.domain.repositories.CountryRepository;
import io.github.jhipster.web.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
@Slf4j
public class AdministrativeDivisionResource {
    private final AdministrativeDivisionRepository administrativeDivisionRepository;
    private final CountryRepository countryRepository;

    /**
     * GET  /administrative-divisions/country/{countryCode} : get all the level 1 administrativeDivisions for country with id.
     *
     * @return the ResponseEntity with province 200 (OK) and the list of all level 1 AdministrativeDivisions in body
     */
    @GetMapping("/administrative-divisions/country/{countryCode}")
    public List<AdministrativeDivision> getAllLevelOneAdministrativeDivisionsByCountry(@PathVariable String countryCode) {
        return countryRepository.findByCode(countryCode)
            .map(administrativeDivisionRepository::findByCountryAndParentIsNull).orElse(new ArrayList<>());
    }

    /**
     * GET  /administrative-divisions/parent/{parentId} : get all the level 1 administrativeDivisions for parent with id.
     *
     * @return the ResponseEntity with province 200 (OK) and the list of AdministrativeDivisions in body
     */
    @GetMapping("/administrative-divisions/parent/{parentId}")
    public List<AdministrativeDivision> getAdministrativeDivisionsByParent(@PathVariable Long parentId) {
        return administrativeDivisionRepository.findById(parentId)
            .map(administrativeDivisionRepository::findByParent).orElse(new ArrayList<>());
    }

    /**
     * GET  /administrative-divisions/:id : get the "id" administrativeDivision.
     *
     * @param id the id of the administrativeDivision to retrieve
     * @return the ResponseEntity with link 200 (OK) and with body the link, or with 404 (Not Found)
     */
    @GetMapping("/administrative-divisions/{id}")
    public ResponseEntity<AdministrativeDivision> getById(@PathVariable Long id) {
        Optional<AdministrativeDivision> division = administrativeDivisionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(division);
    }
}
