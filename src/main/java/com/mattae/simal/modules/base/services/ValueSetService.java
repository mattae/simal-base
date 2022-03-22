package com.mattae.simal.modules.base.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.mattae.simal.modules.base.domain.entities.ValueSet;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValueSetService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;

    public Optional<ValueSet.BaseView> getById(Long id) {
        ValueSet.BaseView view = evm.find(em, ValueSet.BaseView.class, id);
        return Optional.ofNullable(view);
    }

    @Transactional
    public ValueSet.UpdateView saveValue(ValueSet.BaseView value) {
        evm.save(em, value);
        return evm.convert(value, ValueSet.UpdateView.class);
    }

    @Transactional
    public List<ValueSet.UpdateView> saveValues(List<ValueSet.BaseView> values) {
        return values.stream()
            .map(value -> {
                evm.save(em, value);
                return evm.convert(value, ValueSet.UpdateView.class);
            }).collect(Collectors.toList());
    }

    public List<ValueSet.BaseView> getValues(String type, String provider, Boolean active, String lang) {
        var settings = EntityViewSetting.create(ValueSet.BaseView.class);
        var cb = cbf.create(em, ValueSet.class);
        cb.where("type").eq(type)
            .where("provider").eq(provider);
        if (active != null) {
            cb = cb.where("active").eq(active);
        }
        if (lang != null) {
            // @formatter:off
            cb = cb.whereOr()
                    .where("lang").eq(lang)
                .where("lang").isNull()
                .endOr();
            // @formatter:on
        }

        cb.orderBy("code", true);
        var query = evm.applySetting(settings, cb);
        return query.getResultList();
    }

    public String getDisplay(String type, String provider, String lang, String code) {
        var settings = EntityViewSetting.create(ValueSet.DisplayView.class);
        var cb = cbf.create(em, ValueSet.class);
        cb.where("type").eq(type)
            .where("provider").eq(provider)
            .where("active").eq(true)
            .where("code").eq(StringUtils.trimToEmpty(code));
        if (lang != null) {
            // @formatter:off
            cb = cb.whereOr()
                .where("lang").eq(lang)
                .where("lang").isNull()
                .endOr();
            // @formatter:on
        }
        var query = evm.applySetting(settings, cb);
        var result = query.getResultList();
        if (!result.isEmpty()) {
            return result.get(0).getDisplay();
        }
        return "";
    }
}
