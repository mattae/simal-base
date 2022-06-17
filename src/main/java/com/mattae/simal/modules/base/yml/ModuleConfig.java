package com.mattae.simal.modules.base.yml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattae.simal.modules.base.domain.entities.Configuration;
import com.mattae.simal.modules.base.domain.entities.Menu;
import com.mattae.simal.modules.base.domain.entities.ValueSet;
import com.mattae.simal.modules.base.domain.entities.WebRemote;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import javax.validation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
public class ModuleConfig {
    private String name;
    private String basePackage;
    private String version;
    private boolean store = true;
    private String description;
    private String author;
    private String email;
    private String url;
    private String image;
    private Date buildDate;
    private String configurationsPath;
    private String valueSetsPath;
    private List<Translation> translations = new ArrayList<>();
    private List<Dependency> dependencies = new ArrayList<>();
    private List<WebRemote> webRemotes = new ArrayList<>();
    private List<Permission> permissions = new ArrayList<>();
    private List<Role> roles = new ArrayList<>();
    private List<Menu> menus = new ArrayList<>();


    public void validateResources() throws IOException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        List<ConstraintViolation<?>> violations = new ArrayList<>();
        if (configurationsPath != null) {
            List<Configuration> configurations = new ObjectMapper().readValue(
                new ClassPathResource(configurationsPath).getURL(), new TypeReference<>() {
                });
            configurations.forEach(configuration -> {
                violations.addAll(validator.validate(configuration));
            });
        }
        if (valueSetsPath != null) {
            List<ValueSet> valueSets = new ObjectMapper().readValue(
                new ClassPathResource(valueSetsPath).getURL(), new TypeReference<>() {
                });
            valueSets.forEach(valueSet -> {
                violations.addAll(validator.validate(valueSet));
            });
        }

        if (!violations.isEmpty()) {
            throw new ValidationException(violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("\n")));
        }

        for (Translation translation : translations) {
            try {
                new ClassPathResource(translation.getPath()).getURL().openConnection();
            } catch (IllegalArgumentException e) {
                LOG.error("Translation path is required");
                throw e;
            }
        }
    }
}
