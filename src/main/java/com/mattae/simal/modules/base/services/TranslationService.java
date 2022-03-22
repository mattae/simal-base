package com.mattae.simal.modules.base.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mattae.simal.modules.base.domain.entities.Translation;
import com.mattae.simal.modules.base.domain.repositories.TranslationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TranslationService {
    private final TranslationsRepository translationsRepository;

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

        List<Translation> translations = translationsRepository.getByLang(lang);
        translations.sort(Comparator.comparing(Translation::getOrder).thenComparing(Translation::getId));
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
