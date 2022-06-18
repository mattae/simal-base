package com.mattae.simal.modules.base.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mattae.simal.modules.base.domain.entities.Translation;
import com.mattae.simal.modules.base.domain.repositories.TranslationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TranslationService {
    private final TranslationsRepository translationsRepository;
    private final EntityViewManager evm;
    private final CriteriaBuilderFactory cbf;
    private final EntityManager em;

    public static void merge(ObjectNode primary, ObjectNode backup) {
        Iterator<String> fieldNames = backup.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode primaryValue = primary.get(fieldName);
            if (primaryValue == null || !primaryValue.isContainerNode()) {
                JsonNode backupValue = backup.get(fieldName).deepCopy();
                primary.set(fieldName, backupValue);
            } else if (primaryValue.isObject()) {
                JsonNode backupValue = backup.get(fieldName);
                if (backupValue.isObject()) {
                    merge((ObjectNode) primaryValue, backupValue.deepCopy());
                }
            }
        }
    }

    public Optional<Translation> getById(Long id) {
        return translationsRepository.findById(id);
    }

    @Transactional
    public Translation save(Translation translation) {
        return translationsRepository.save(translation);
    }

    public JsonNode listByLang(String lang) {
        var settings = EntityViewSetting.create(Translation.View.class);
        var cb = cbf.create(em, Translation.class);
        // @formatter:off
        cb.where("lang").eq(lang)
            .whereOr()
                .where("module").isNull()
                .where("module.started").eq(true)
            .endOr();
        // @formatter:on
        List<Translation.View> translations = evm.applySetting(settings, cb).getResultList();
        translations.sort(Comparator.comparing(Translation.View::getOrder).thenComparing(Translation.View::getId));
        var ref = new Object() {
            JsonNode primary = new ObjectMapper().createObjectNode();
        };
        if (!translations.isEmpty()) {
            ref.primary = translations.get(0).getData();
        }
        if (translations.size() > 1) {
            translations.subList(1, translations.size())
                .forEach(translation -> {
                    merge((ObjectNode) ref.primary, (ObjectNode) translation.getData());
                });
        }
        return ref.primary;
    }

    @Transactional
    public void deleteById(Long id) {
        translationsRepository.deleteById(id);
    }
}
