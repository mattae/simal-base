package com.mattae.simal.modules.base.web.rest;

import com.mattae.simal.modules.base.domain.repositories.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.mattae.simal.modules.base.domain.entities.Country;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing Country.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class CountryResource {

    private final CountryRepository countryRepository;

    /**
     * GET  /countries : get all the countries.
     *
     * @return the ResponseEntity with countries 200 (OK) and the list of countries in body
     */
    /*@GetMapping("/countries")
    public List<Country> getAllCountries() {
        LOG.debug("REST request to get all Countries");
        return countryRepository.findAll();
    }*/
}
