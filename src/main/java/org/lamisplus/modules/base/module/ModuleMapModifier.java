package org.lamisplus.modules.base.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entities.Module;
import org.lamisplus.modules.base.domain.repositories.ModuleRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleMapModifier {
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final ModuleRepository moduleRepository;

    public void modifyModuleMap(ApplicationContext context) {
        context.getBeansOfType(ModuleMapModifierProvider.class)
                .values().forEach(provider -> {
            Map<String, String> moduleMap = new HashMap<>();
            Module module = provider.getModuleToModify();
            if (module != null) {
                try {
                    if (module.getModuleMap() != null) {
                        moduleMap = OBJECT_MAPPER.readValue(module.getModuleMap(), new TypeReference<Map<String, String>>() {
                        });
                    }
                } catch (IOException ignored) {
                }
                try {
                    if (provider.getModuleMap() != null) {
                        Map<String, String> map = OBJECT_MAPPER.readValue(provider.getModuleMap(), new TypeReference<Map<String, String>>() {
                        });
                        map.forEach(moduleMap::put);
                    }
                    moduleMap.put(provider.getAngularModuleName(), provider.getUmdUrl());
                } catch (IOException ignored) {
                }
                try {
                    if (provider.reset()) {
                        moduleMap.clear();
                        if (provider.getUmdLocation() != null) {
                            module.setUmdLocation(provider.getUmdLocation());
                        }
                    }
                    module.setModuleMap(OBJECT_MAPPER.writeValueAsString(moduleMap));
                } catch (JsonProcessingException ignored) {
                }
                moduleRepository.save(module);
            }
        });
    }
}
