package org.lamisplus.modules.base.web.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entities.Country;
import org.lamisplus.modules.base.domain.repositories.CountryRepository;
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
@Slf4j
public class CountryResource {

    private static final String ENTITY_NAME = "country";

    private final CountryRepository countryRepository;

    /**
     * GET  /countries : get all the countries.
     *
     * @return the ResponseEntity with countries 200 (OK) and the list of countries in body
     */
    @GetMapping("/countries")
    public List<Country> getAllCountries() {
        LOG.debug("REST request to get all Countries");
        return countryRepository.findAll();
    }
}
