package org.lamisplus.modules.base.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entities.State;
import org.lamisplus.modules.base.domain.repositories.CountryRepository;
import org.lamisplus.modules.base.domain.repositories.StateRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing State.
 */
@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class StateResource {

    private final StateRepository stateRepository;
    private final CountryRepository countryRepository;

    /**
     * GET  /states/country/:code : get all states by country :code (id or ISO code)
     *
     * @return the ResponseEntity with states 200 (OK) and the list of states in body
     */
    /*@GetMapping("/states/country/{code}")
        public List<State> getStatesByCountryISOCode(@PathVariable String code) {
        LOG.debug("REST request to get all States by country ISO Code: {}", code);

        Optional<Country> country;
        if (StringUtils.isNumeric(code)) {
            country = countryRepository.findById(Long.parseLong(code));
        } else {
            country = countryRepository.findByCode(code);
        }
        return country.map(stateRepository::findByCountry)
                .orElse(new ArrayList<>());
    }*/

    /**
     * GET  /states/:id : get the "id" link.
     *
     * @param id the id of the link to retrieve
     * @return the ResponseEntity with link 200 (OK) and with body the link, or with 404 (Not Found)
     */
    @GetMapping("/states/{id}")
    public ResponseEntity<State> getState(@PathVariable Long id) {
        LOG.debug("REST request to get State : {}", id);
        Optional<State> state = stateRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(state);
    }

    @GetMapping("/states")
    @Cacheable(cacheNames = "states")
    public List<State> getStates() {
        LOG.debug("Request to get all states");
        return stateRepository.findAll();
    }
}
