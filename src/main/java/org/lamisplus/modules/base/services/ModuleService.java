package org.lamisplus.modules.base.services;

import com.github.zafarkhaja.semver.Version;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.lamisplus.modules.base.domain.entities.Module;
import org.lamisplus.modules.base.domain.repositories.ModuleRepository;
import org.lamisplus.modules.base.module.ModuleFileStorageService;
import org.lamisplus.modules.base.module.ModuleUtils;
import org.lamisplus.modules.base.services.dto.ModuleDependencyDTO;
import org.lamisplus.modules.base.yml.ModuleConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final ModuleFileStorageService storageService;

    public Optional<Module> getModule(String id) {
        return moduleRepository.findById(id);
    }

    public Module activate(Module module) {
        LOG.debug("Activating module {} ...", module);
        module.setActive(true);
        return moduleRepository.save(module);
    }

    public Module deactivate(Module module) {
        LOG.debug("Deactivating module {} ...", module);
        module.setActive(false);
        return moduleRepository.save(module);
    }

    public List<Module> getModules() {
        return moduleRepository.findAll();
    }

    @SneakyThrows
    @Transactional
    public Module installOrUpdate(Module updateModule) {
        Module module = moduleRepository.findByName(updateModule.getName()).orElse(updateModule);
        module.setVersion(updateModule.getVersion());
        module.setDescription(updateModule.getDescription());
        module.setBasePackage(updateModule.getBasePackage());
        module.setBuildTime(updateModule.getBuildTime());
        module.setArtifact(updateModule.getArtifact());
        module.setActive(true);
        module.setProcessConfig(true);
        module = moduleRepository.save(module);

        saveModuleData(module);
        return module;
    }

    public void uninstall(String id) {
        moduleRepository.findById(id).ifPresent(module -> {
            module.setUninstall(true);
            moduleRepository.save(module);
        });
    }

    @SneakyThrows
    public Module uploadModuleData(MultipartFile file) {
        Module module = new Module();
        ModuleConfig config = ModuleUtils.loadModuleConfig(file.getInputStream(), "module.yml");
        String fileName = storageService.store(config.getName(), file);
        URLClassLoader classLoader = new URLClassLoader(new URL[]{storageService.getURL(fileName)});
        URL url = classLoader.findResource("META-INF/MANIFEST.MF");
        Manifest manifest = new Manifest(url.openStream());
        Attributes attributes = manifest.getMainAttributes();
        module.setVersion(attributes.getValue("Implementation-Version"));
        module.setDescription(attributes.getValue("Implementation-Title"));
        if (StringUtils.isNotBlank(config.getSummary())) {
            module.setDescription(config.getSummary());
        }
        try {
            Date date = DateUtils.parseDate(attributes.getValue("Build-Time"), "yyyyMMdd-HHmm",
                "yyyy-MM-dd'T'HH:mm:ss'Z'");
            module.setBuildTime(date);
        } catch (Exception ignored) {
        }
        module.setArtifact(StringUtils.replace(fileName, "\\", "/"));
        module.setName(config.getName());
        module.setBasePackage(config.getBasePackage());
        return module;
    }

    @SneakyThrows
    @Transactional
    public List<ModuleDependencyDTO> getDependencies(String id) {
        List<ModuleDependencyDTO> dependencies = new ArrayList<>();
        moduleRepository.findById(id).ifPresent(module -> {
            try {
                InputStream inputStream;
                byte[] data = module.getData();
                if (data != null) {
                    inputStream = new ByteArrayInputStream(data);
                } else {
                    inputStream = storageService.readFile(module.getArtifact());
                }
                ModuleConfig config = ModuleUtils.loadModuleConfig(inputStream, "module.yml");
                if (config != null) {
                    config.getDependencies()
                        .forEach(dependency -> {
                            String name = dependency.getName();
                            String version = dependency.getVersion();

                            ModuleDependencyDTO dto = new ModuleDependencyDTO();
                            dto.setName(name);
                            dto.setRequiredVersion(version);
                            moduleRepository.findByName(name).ifPresent(m -> {
                                dto.setId(m.getId());
                                dto.setActive(m.getActive());
                                dto.setInstalledVersion(m.getVersion());
                                Version installed = Version.valueOf(m.getVersion());
                                dto.setVersionSatisfied(installed.satisfies(version));
                            });
                            dependencies.add(dto);
                        });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return dependencies;
    }

    private void saveModuleData(Module module) {
        try {
            InputStream stream = storageService.readFile(module.getArtifact());
            byte[] data = IOUtils.toByteArray(stream);
            ModuleConfig config = ModuleUtils.loadModuleConfig(new ByteArrayInputStream(data), "module.yml");
            if (config != null && config.isStore()) {
                module.setData(data);
                moduleRepository.save(module);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
