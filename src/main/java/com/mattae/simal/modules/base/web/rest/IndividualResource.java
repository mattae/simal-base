package com.mattae.simal.modules.base.web.rest;

import com.mattae.simal.modules.base.domain.entities.Individual;
import com.mattae.simal.modules.base.services.IndividualService;
import com.mattae.simal.modules.base.services.errors.DataValidationException;
import com.mattae.simal.modules.base.web.rest.vm.PagedResult;
import com.mattae.simal.modules.base.web.rest.vm.SearchVM;
import io.github.jhipster.web.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/individuals")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class IndividualResource {
    private final IndividualService individualService;

    @PostMapping
    public Individual.CreateView create(@RequestBody @Valid Individual.CreateView individual) {
        return individualService.save(individual);
    }

    @PutMapping
    public Individual.CreateView update(@RequestBody @Valid Individual.UpdateView individual) {
        return individualService.save(individual);
    }

    @GetMapping
    public PagedResult<Individual.View> list(SearchVM search) {
        return individualService.list(search.getKeyword(), search.getActive(), search.getStart(), search.getPageSize());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Individual.CreateView> getById(@PathVariable UUID id) {
        return ResponseUtil.wrapOrNotFound(individualService.getById(id));
    }
}
