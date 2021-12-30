package com.mattae.simal.modules.base.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.mattae.simal.modules.base.domain.entities.ValueSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValueSetService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;

    public Optional<ValueSet.BaseView> getById(Long id) {
        ValueSet.BaseView view = evm.find(em, ValueSet.BaseView.class, id);
        return Optional.ofNullable(view);
    }

    @Transactional
    public ValueSet.UpdateView saveValue(ValueSet.UpdateView value) {
        evm.save(em, value);
        return evm.find(em, ValueSet.UpdateView.class, value.getId());
    }

    @Transactional
    public List<ValueSet.UpdateView> saveValues(List<ValueSet.UpdateView> values) {
        return values.stream()
            .map(value -> {
                evm.save(em, value);
                return evm.find(em, ValueSet.UpdateView.class, value.getId());
            }).collect(Collectors.toList());
    }

    public List<ValueSet.BaseView> getValues(String type, String provider, Boolean active) {
        var settings = EntityViewSetting.create(ValueSet.BaseView.class);
        var cb = cbf.create(em, ValueSet.class);
        cb.where("type").eq(type)
            .where("provider").eq(provider)
            .where("active").eq(active)
            .orderBy("value", true);
        var query = evm.applySetting(settings, cb);
        return query.getResultList();
    }
}
