package org.lamisplus.modules.base.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entities.LocalGovernment;
import org.lamisplus.modules.base.domain.entities.State;
import org.lamisplus.modules.base.domain.repositories.LocalGovernmentRepository;
import org.lamisplus.modules.base.domain.repositories.StateRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Province.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class LocalGovernmentResource {
    private final LocalGovernmentRepository localGovernmentRepository;
    private final StateRepository stateRepository;

    /**
     * GET  /provinces/link/stateId : get all the localAuthorities for link with id.
     *
     * @return the ResponseEntity with province 200 (OK) and the list of Province in body
     */
    @GetMapping("/local-governments/state/{stateId}")
    public List<LocalGovernment> getAllProvinceByStateId(@PathVariable Long stateId) {
        LOG.debug("REST request to get all Province for state: {}", stateId);

        Optional<State> state = stateRepository.findById(stateId);
        return state.map(localGovernmentRepository::findByStateOrderByName)
            .orElse(new ArrayList<>());
    }

    /**
     * GET  /provinces/:id : get the "id" province.
     *
     * @param id the id of the province to retrieve
     * @return the ResponseEntity with provinces 200 (OK) and with body the list of provinces for the state
     */
    @GetMapping("/local-governments/{id}")
    public ResponseEntity<LocalGovernment> getProvinces(@PathVariable Long id) {
        LOG.debug("REST request to get Province : {}", id);
        Optional<LocalGovernment> province = localGovernmentRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(province);
    }

    @GetMapping("/local-governments/{id}/state")
    public ResponseEntity<State> getStateByProvince(@PathVariable Long id) {
        LOG.debug("REST request to get State for Province : {}", id);
        Optional<LocalGovernment> province = localGovernmentRepository.findById(id);
        return province.map(value -> ResponseEntity.ok(value.getState())).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
