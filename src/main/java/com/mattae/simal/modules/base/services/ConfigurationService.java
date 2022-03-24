package com.mattae.simal.modules.base.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mattae.simal.modules.base.domain.entities.Configuration;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mattae.simal.modules.base.services.TranslationService.merge;

@Service
@RequiredArgsConstructor
public class ConfigurationService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;
    private final ObjectMapper objectMapper;

    @Transactional
    public Configuration.View update(Configuration.View configuration) {
        evm.save(em, configuration);
        return configuration;
    }

    public List<Configuration.View> list(String category, String key) {
        var settings = EntityViewSetting.create(Configuration.View.class);
        var cb = cbf.create(em, Configuration.View.class);
        cb = cb.from(Configuration.class);
        if (StringUtils.isNotBlank(category)) {
            cb.where("category").like(false).value("%" + category + "%").noEscape();
        }
        // @formatter:off
        cb.whereOr()
                .where("module").isNull()
                .where("module.started").eq(true)
            .endOr();
        // @formatter:on

        List<Configuration.View> configurations = evm.applySetting(settings, cb).getResultList();
        if (StringUtils.isNotBlank(key)) {
            return configurations.stream()
                .filter(c -> {
                    Set<Configuration.Data> data = c.getData();
                    c.setData(data.stream()
                        .filter(x -> x.getKey().toLowerCase().contains(key.toLowerCase()))
                        .collect(Collectors.toSet()));
                    return !c.getData().isEmpty();
                }).toList();
        }

        return configurations;
    }

    public Optional<String> getValueAsStringForKey(String category, String key) {
        return findByCategoryAndKey(category, key)
            .map(Configuration.Data::getValue);
    }

    public Optional<Boolean> getValueAsBooleanForKey(String category, String key) {
        return findByCategoryAndKey(category, key)
            .map(data -> {
                try {
                    return Boolean.valueOf(data.getValue());
                } catch (Exception e) {
                    return null;
                }
            });
    }

    public Optional<Double> getValueAsNumericForKey(String category, String key) {
        return findByCategoryAndKey(category, key)
            .map(data -> {
                try {
                    return Double.valueOf(data.getValue());
                } catch (Exception e) {
                    return null;
                }
            });
    }

    public Optional<LocalDate> getValueAsDateForKey(String category, String key) {
        return findByCategoryAndKey(category, key)
            .map(data -> {
                try {
                    return LocalDate.parse(data.getValue(), DateTimeFormatter.ISO_DATE);
                } catch (Exception e) {
                    return null;
                }
            });
    }

    private Optional<Configuration.Data> findByCategoryAndKey(String category, String key) {
        var settings = EntityViewSetting.create(Configuration.View.class);
        var cb = cbf.create(em, Configuration.View.class);

        // @formatter:off
        cb.from(Configuration.class)
            .where("category").eq(category)
            .whereOr()
                .where("module").isNull()
                .where("module.started").eq(true)
            .endOr();
        // @formatter:on

        var ref = new Object() {
            JsonNode primary = new ObjectMapper().createObjectNode();
        };
        List<Configuration.Data> data = evm.applySetting(settings, cb).getResultList()
            .stream()
            .sorted(Comparator.comparing(Configuration.View::getOrder).thenComparing(Configuration.View::getId))
            .flatMap(c -> c.getData().stream())
            .filter(d -> d.getKey().toLowerCase().contains(key.toLowerCase()))
            .toList();

        if (!data.isEmpty()) {
            ref.primary = configurationDataToJsonNode(data.get(0));
            if (data.size() > 1) {
                data.subList(1, data.size())
                    .forEach(d -> {
                        merge((ObjectNode) ref.primary, (ObjectNode) configurationDataToJsonNode(d));
                    });
            }
            return Optional.of(objectMapper.convertValue(ref.primary, Configuration.Data.class));
        }
        return Optional.empty();
    }

    private JsonNode configurationDataToJsonNode(Configuration.Data data) {
        return objectMapper.convertValue(data, JsonNode.class);
    }
}
