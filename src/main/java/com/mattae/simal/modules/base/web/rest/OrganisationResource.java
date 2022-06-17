package com.mattae.simal.modules.base.web.rest;

import com.mattae.simal.modules.base.domain.entities.Organisation;
import com.mattae.simal.modules.base.services.OrganisationService;
import com.mattae.simal.modules.base.web.errors.DataValidationException;
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
@RequestMapping("/api/organisations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class OrganisationResource {
    private final OrganisationService organisationService;

    @PostMapping
    public Organisation.CreateView create(@RequestBody @Valid Organisation.CreateView organisation) {
        return organisationService.save(organisation);
    }

    @PutMapping
    public Organisation.CreateView update(@RequestBody @Valid Organisation.UpdateView organisation) {
        if (organisation.getId() == null) {
            throw new DataValidationException("Cannot update organisation without id");
        }
        return organisationService.update(organisation);
    }

    @GetMapping
    public PagedResult<Organisation.View> list(SearchVM search) {
        return organisationService.list(search.getKeyword(), search.getType(), search.getActive(), search.getStart(),
            search.getPageSize());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Organisation.CreateView> getById(@PathVariable UUID id) {
        return ResponseUtil.wrapOrNotFound(organisationService.getById(id));
    }
}
