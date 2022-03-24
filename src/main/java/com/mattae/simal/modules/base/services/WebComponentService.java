package com.mattae.simal.modules.base.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.mattae.simal.modules.base.domain.entities.ExposedComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebComponentService {
    private final EntityManager em;
    private final EntityViewManager evm;
    private final CriteriaBuilderFactory cbf;


    public Optional<ExposedComponent.View> findByName(UUID name) {
        var settings = EntityViewSetting.create(ExposedComponent.View.class);
        var cb = cbf.create(em, ExposedComponent.class);
        cb.where("name").eq(name);
        return evm.applySetting(settings, cb).getResultList().stream().findFirst();
    }
}
