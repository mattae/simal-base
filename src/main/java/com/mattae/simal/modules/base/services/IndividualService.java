package com.mattae.simal.modules.base.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.foreach.across.modules.user.business.User;
import com.mattae.simal.modules.base.domain.entities.Individual;
import com.mattae.simal.modules.base.domain.views.UserView;
import com.mattae.simal.modules.base.web.rest.vm.PagedResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndividualService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;

    @Transactional
    public Individual.CreateView save(Individual.CreateView value) {
        evm.save(em, value);
        return value;
    }

    @Transactional
    public Individual.CreateView update(Individual.UpdateView value) {
        evm.save(em, value);
        return value;
    }

    public Optional<Individual.CreateView> getById(UUID id) {
        return Optional.ofNullable(evm.find(em, Individual.CreateView.class, id));
    }

    public PagedResult<Individual.View> list(String keyword, Boolean active, int start, int pageSize) {
        var settings = EntityViewSetting.create(Individual.View.class, start, pageSize);
        var cb = cbf.create(em, Individual.class);
        if (StringUtils.isNotBlank(keyword)) {
            keyword = "%" + keyword + "%";
            settings.addAttributeFilter("name.givenName", keyword);
            settings.addAttributeFilter("name.familyName", keyword);
            settings.addAttributeFilter("name.middleName", keyword);
            settings.addAttributeFilter("email", keyword);
            settings.addAttributeFilter("phoneNumber", keyword);
        }
        if (active == null) {
            active = true;
        }
        settings.addAttributeFilter("active", active);
        var query = evm.applySetting(settings, cb);
        query.withCountQuery(true);
        return new PagedResult<>(query.getResultList(), query.getResultList().getTotalSize(), query.getResultList().getTotalPages());
    }

    @PostConstruct
    public void init() {
        var settings = EntityViewSetting.create(UserView.class);
        var cb = cbf.create(em, User.class);
    }
}
