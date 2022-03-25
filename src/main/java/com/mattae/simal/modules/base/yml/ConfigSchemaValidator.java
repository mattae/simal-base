package com.mattae.simal.modules.base.yml;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mattae.simal.modules.base.BaseModule;
import com.mattae.simal.modules.base.domain.enumeration.MenuType;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Set;

@Slf4j
public class ConfigSchemaValidator {
    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper(new YAMLFactory());
        MAPPER.addMixIn(MenuType.class, MixIn.class);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @SneakyThrows
    public static boolean isValid(String config, Boolean... validateResources) {
        URI schemaFile = BaseModule.class.getClassLoader().getResource("config-schema.json").toURI();
        JsonSchemaFactory factory = JsonSchemaFactory.builder(JsonSchemaFactory
            .getInstance(SpecVersion.VersionFlag.V201909)).objectMapper(MAPPER).build();

        Set<ValidationMessage> invalidMessages = factory.getSchema(schemaFile)
            .validate(MAPPER.readTree(config));
        if (!invalidMessages.isEmpty()) {
            LOG.debug("Schema validation failed: {}", config);
            invalidMessages.forEach(m -> LOG.debug("...{}", m.getMessage()));
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MAPPER.writeValue(baos, MAPPER.readTree(config));
            Yaml yaml = new Yaml(new Constructor(ModuleConfig.class));
            yaml.load(baos.toString());

            ModuleConfig moduleConfig = MAPPER.readValue(config, ModuleConfig.class);
            if (validateResources.length == 0 || validateResources[0]) {
                moduleConfig.validateResources();
            }
        } catch (Exception e) {
            LOG.error("Error: {}", e.getMessage());
            return false;
        }

        return invalidMessages.isEmpty();
    }

    abstract class MixIn {
        @JsonValue(false)
        abstract String getType();
    }
}
