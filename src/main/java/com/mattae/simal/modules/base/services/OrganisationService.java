package com.mattae.simal.modules.base.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.foreach.across.core.annotations.Exposed;
import com.mattae.simal.modules.base.domain.entities.Organisation;
import com.mattae.simal.modules.base.web.rest.vm.PagedResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;

@Service
@Exposed
@RequiredArgsConstructor
public class OrganisationService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;

    @Transactional
    public Organisation.CreateView save(Organisation.CreateView value) {
        evm.save(em, value);
        return value;
    }

    @Transactional
    public Organisation.CreateView update(Organisation.UpdateView value) {
        evm.save(em, value);
        return value;
    }

    public Optional<Organisation.CreateView> getById(UUID id) {
        return Optional.ofNullable(evm.find(em, Organisation.CreateView.class, id));
    }

    public PagedResult<Organisation.View> list(String keyword, String type, Boolean active, int start, int pageSize) {
        var settings = EntityViewSetting.create(Organisation.View.class, start, pageSize);
        var cb = cbf.create(em, Organisation.View.class);
        if (StringUtils.isNotBlank(keyword)) {
            keyword = "%" + keyword + "%";
            settings.addAttributeFilter("name", keyword);
            settings.addAttributeFilter("email", keyword);
            settings.addAttributeFilter("phoneNumber", keyword);
        }
        if (StringUtils.isNotBlank(type)) {
            settings.addAttributeFilter("type", type);
        }
        if (active == null) {
            active = true;
        }
        settings.addAttributeFilter("active", active);
        var query = evm.applySetting(settings, cb);
        query.withCountQuery(true);
        return new PagedResult<>(query.getResultList(), query.getResultList().getTotalSize(), query.getResultList().getTotalPages());
    }
}
