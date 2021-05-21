package com.mattae.simal.modules.base.services;

import com.foreach.across.modules.filemanager.business.reference.FileReference;
import com.foreach.across.modules.filemanager.business.reference.FileReferenceRepository;
import com.foreach.across.modules.filemanager.business.reference.FileReferenceService;
import com.foreach.across.modules.filemanager.business.reference.properties.FileReferenceProperties;
import com.foreach.across.modules.filemanager.business.reference.properties.FileReferencePropertiesService;
import com.foreach.across.modules.filemanager.services.FileManager;
import com.github.zafarkhaja.semver.Version;
import com.mattae.simal.modules.base.domain.entities.Module;
import com.mattae.simal.modules.base.domain.repositories.ModuleRepository;
import com.mattae.simal.modules.base.module.ModuleUtils;
import com.mattae.simal.modules.base.services.dto.ModuleDependencyDTO;
import com.mattae.simal.modules.base.yml.ModuleConfig;
import com.mattae.simal.modules.base.config.ApplicationConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleService {
    public static final String MODULE_DIR = "module-data";
    private final ModuleRepository moduleRepository;
    private final FileManager fileManager;
    private final FileReferenceService fileReferenceService;
    private final FileReferencePropertiesService fileReferencePropertiesService;
    private final FileReferenceRepository fileReferenceRepository;

    public Optional<Module> getModule(String id) {
        return moduleRepository.findById(id).stream()
            .map(Module::copy)
            .findFirst();
    }

    public Module activate(Module module) {
        module.setActive(true);
        return moduleRepository.save(module);
    }

    public Module deactivate(Module module) {
        module.setActive(false);
        return moduleRepository.save(module);
    }

    public List<Module> getModules() {
        return moduleRepository.findAll().stream()
            .map(Module::copy)
            .collect(Collectors.toList());
    }

    @SneakyThrows
    @Transactional
    public Module installOrUpdate(Module updateModule) {
        final Module module = moduleRepository.findByName(updateModule.getName()).orElse(updateModule);
        module.setVersion(updateModule.getVersion());
        module.setDescription(updateModule.getDescription());
        module.setBasePackage(updateModule.getBasePackage());
        module.setBuildTime(updateModule.getBuildTime());
        module.setActive(true);
        module.setProcessConfig(true);
        fileReferencePropertiesService.getEntityIdsForPropertyValue("name", module.getName())
            .forEach(id -> {
                FileReference reference = fileReferenceRepository.getOne(id);
                module.setFile(reference);
            });
        Module module1 = moduleRepository.save(module);
        saveModuleData(module1);
        return module1;
    }

    public void uninstall(String id) {
        moduleRepository.findById(id).ifPresent(module -> {
            module.setUninstall(true);
            moduleRepository.save(module);
        });
    }

    @SneakyThrows
    @Transactional
    public Module uploadModuleData(MultipartFile file) {
        Module module = new Module();
        ModuleConfig config = ModuleUtils.loadModuleConfig(file.getInputStream(), "module.yml");
        FileReference fileReference = fileReferenceService.save(file, ApplicationConfiguration.TEMP_MODULE_DIR);
        LOG.info("Reference: {}", fileReference.getFileDescriptor());
        fileReferenceRepository.flush();
        FileReferenceProperties properties = fileReferencePropertiesService.getProperties(fileReference.getId());
        properties.put("name", config.getName());
        fileReferencePropertiesService.saveProperties(properties);

        Path tmp = Files.createTempFile("module", null);
        fileManager.getFileResource(fileReference.getFileDescriptor()).copyTo(tmp.toFile());
        URLClassLoader classLoader = new URLClassLoader(new URL[]{tmp.toUri().toURL()});
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
                InputStream inputStream = null;
                byte[] data = module.getData();
                if (data != null) {
                    inputStream = new ByteArrayInputStream(data);
                } else {
                    Collection<Long> ids = fileReferencePropertiesService.getEntityIdsForPropertyValue("name", module.getName());
                    if (!ids.isEmpty()) {
                        FileReference reference = fileReferenceRepository.getOne(ids.iterator().next());
                        inputStream = fileManager.getFileResource(reference.getFileDescriptor()).getInputStream();
                    }
                }
                Assert.notNull(inputStream, "Cannot read module data");
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

    @SneakyThrows
    private void saveModuleData(Module module) {
        Collection<Long> ids = fileReferencePropertiesService.getEntityIdsForPropertyValue("name", module.getName());
        if (!ids.isEmpty()) {
            FileReference reference = fileReferenceRepository.getOne(ids.iterator().next());
            InputStream stream = fileManager.getFileResource(reference.getFileDescriptor()).getInputStream();
            byte[] data = IOUtils.toByteArray(stream);
            ModuleConfig config = ModuleUtils.loadModuleConfig(new ByteArrayInputStream(data), "module.yml");
            if (config != null && config.isStore()) {
                module.setData(data);
                module.setFile(null);
                fileReferenceService.delete(reference, true);
                moduleRepository.save(module);
            } else if (config != null && !config.isStore()) {
                fileReferenceService.changeFileRepository(reference, MODULE_DIR);
            }
        }
    }
}
