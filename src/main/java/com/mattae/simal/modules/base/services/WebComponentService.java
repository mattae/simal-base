package com.mattae.simal.modules.base.services;

import com.mattae.simal.modules.base.services.dto.ComponentDTO;
import com.mattae.simal.modules.base.domain.repositories.ExposedComponentRepository;
import lombok.RequiredArgsConstructor;
import com.mattae.simal.modules.base.domain.entities.Module;
import com.mattae.simal.modules.base.domain.entities.WebComponent;
import com.mattae.simal.modules.base.domain.repositories.WebComponentRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WebComponentService {
    private final WebComponentRepository webComponentRepository;
    private final ExposedComponentRepository exposedComponentRepository;

    @PostAuthorize("returnObject != null && returnObject.authorities.size() > 0 ? hasAnyAuthority(returnObject.authorities) : true ")
    public ComponentDTO getComponentById(String id) {
        return webComponentRepository.findById(id).map(this::getDto).orElse(null);
    }

    @PostFilter("filterObject.authorities.size() > 0 ? hasAnyAuthority(filterObject.authorities) : true ")
    public List<ComponentDTO> getComponentsByType(String type) {
        return webComponentRepository.findByType(type).stream()
            .map(this::getDto).collect(Collectors.toList());
    }

    private ComponentDTO getDto(WebComponent c) {
        String componentId = c.getComponentId();
        return exposedComponentRepository.findByUuid(componentId).map(ec -> {
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
