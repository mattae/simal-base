package com.mattae.simal.modules.base.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.mattae.simal.modules.base.domain.entities.ExposedComponent;
import com.mattae.simal.modules.base.domain.entities.Module;
import com.mattae.simal.modules.base.domain.entities.WebComponent;
import com.mattae.simal.modules.base.domain.repositories.ExposedComponentRepository;
import com.mattae.simal.modules.base.domain.repositories.WebComponentRepository;
import com.mattae.simal.modules.base.services.dto.ComponentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebComponentService {
    private final WebComponentRepository webComponentRepository;
    private final ExposedComponentRepository exposedComponentRepository;
    private final EntityManager em;
    private final EntityViewManager evm;
    private final CriteriaBuilderFactory cbf;

    @PostAuthorize("returnObject != null && returnObject.authorities.size() > 0 ? hasAnyAuthority(returnObject.authorities) : true ")
    public ComponentDTO getComponentById(UUID id) {
        return webComponentRepository.findById(id).map(this::getDto).orElse(null);
    }

    @PostFilter("filterObject.authorities.size() > 0 ? hasAnyAuthority(filterObject.authorities) : true ")
    public List<ComponentDTO> getComponentsByType(String type) {
        return webComponentRepository.findByType(type).stream()
            .map(this::getDto).collect(Collectors.toList());
    }

    public Optional<ExposedComponent.View> findByName(UUID name) {
        var settings = EntityViewSetting.create(ExposedComponent.View.class);
        var cb = cbf.create(em, ExposedComponent.class);
        cb.where("name").eq(name);
        return evm.applySetting(settings, cb).getResultList().stream().findFirst();
    }

    private ComponentDTO getDto(WebComponent c) {
        UUID componentId = c.getComponentId();
        return exposedComponentRepository.findById(componentId).map(ec -> {
            Module module = c.getModule();
            if (module.getStarted()) {
                ComponentDTO dto = new ComponentDTO();
                dto.setRemoteName(ec.getWebRemote().getRemoteName());
                dto.setComponentName(ec.getComponentName());
                dto.setElementName(ec.getElementName());
                dto.setExposedName(ec.getExposedName());
                dto.setAuthorities(ec.getAuthorities());
                return dto;
            }
            return null;
        }).orElse(null);
    }
}
