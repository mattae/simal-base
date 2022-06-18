package com.mattae.simal.modules.base.services;

import com.foreach.across.modules.filemanager.business.reference.FileReference;
import com.foreach.across.modules.filemanager.business.reference.FileReferenceRepository;
import com.foreach.across.modules.filemanager.business.reference.FileReferenceService;
import com.foreach.across.modules.filemanager.business.reference.properties.FileReferenceProperties;
import com.foreach.across.modules.filemanager.business.reference.properties.FileReferencePropertiesService;
import com.foreach.across.modules.filemanager.services.FileManager;
import com.github.zafarkhaja.semver.Version;
import com.mattae.simal.modules.base.config.ApplicationConfiguration;
import com.mattae.simal.modules.base.domain.entities.Module;
import com.mattae.simal.modules.base.domain.repositories.ModuleRepository;
import com.mattae.simal.modules.base.module.ModuleUtils;
import com.mattae.simal.modules.base.services.dto.ModuleDependencyDTO;
import com.mattae.simal.modules.base.web.vm.ModuleVM;
import com.mattae.simal.modules.base.yml.ModuleConfig;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
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
public class ModuleService {
    public static final String MODULE_DIR = "module-data";
    private final ModuleRepository moduleRepository;
    private final FileManager fileManager;
    private final FileReferenceService fileReferenceService;
    private final FileReferencePropertiesService fileReferencePropertiesService;
    private final FileReferenceRepository fileReferenceRepository;

    public Optional<ModuleVM> getModule(UUID id) {
        return moduleRepository.findById(id).stream()
            .map(this::vmFromModule)
            .findFirst();
    }

    @Transactional
    public ModuleVM activate(Module module) {
        return vmFromModule(moduleRepository.findByName(module.getName()).map(m -> {
            m.setActive(true);
            return m;
        }).orElse(module));
    }

    @Transactional
    public ModuleVM deactivate(Module module) {
        return vmFromModule(moduleRepository.findByName(module.getName()).map(m -> {
            m.setActive(false);
            return m;
        }).orElse(module));
    }

    public List<ModuleVM> getModules() {
        return moduleRepository.findAll().stream()
            .map(this::vmFromModule)
            .collect(Collectors.toList());
    }

    @Transactional
    public ModuleVM installOrUpdate(Module updateModule) {
        final Module module = moduleRepository.findByName(updateModule.getName()).orElse(updateModule);
        module.setVersion(updateModule.getVersion());
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
        return vmFromModule(module);
    }

    public void uninstall(UUID id) {
        moduleRepository.findById(id).ifPresent(module -> {
            module.setUninstall(true);
            moduleRepository.save(module);
        });
    }

    @Transactional
    public Module uploadModuleData(MultipartFile file) throws Exception {
        Module module = new Module();
        ModuleConfig config = ModuleUtils.loadModuleConfig(file.getInputStream(), "module.yml");
        fileReferencePropertiesService.getEntityIdsForPropertyValue("name", config.getName())
            .forEach(id -> {
                fileReferenceRepository.findById(id).ifPresent(fileReference -> {
                    fileReferenceService.delete(fileReference, true);
                });
            });
        FileReference fileReference = fileReferenceService.save(file, ApplicationConfiguration.TEMP_MODULE_DIR);
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

    @Transactional
    public List<ModuleDependencyDTO> getDependencies(UUID id) {
        List<ModuleDependencyDTO> dependencies = new ArrayList<>();
        moduleRepository.findById(id).ifPresent(module -> {
            try {
                ModuleConfig config = getConfig(module);
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
                                dto.setStarted(m.getStarted());
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

    @Transactional
    public List<ModuleDependencyDTO> getDependents(UUID moduleId) {
        List<ModuleDependencyDTO> dependents = new ArrayList<>();
        moduleRepository.findById(moduleId).ifPresent(module -> {
            String name = module.getName();
            moduleRepository.findByUninstallIsFalse().forEach(m -> {
                try {
                    getConfig(m).getDependencies().stream()
                        .filter(d -> d.getName().equals(name) && !m.getName().equals(name))
                        .findFirst().ifPresent(dependency -> {
                        ModuleDependencyDTO dependent = new ModuleDependencyDTO();
                        dependent.setId(m.getId());
                        dependent.setName(m.getName());
                        dependent.setActive(m.getActive());
                        dependent.setInstalledVersion(m.getVersion());
                        dependent.setRequiredVersion(dependency.getVersion());
                        dependent.setStarted(m.getStarted());
                        Version installed = Version.valueOf(module.getVersion());
                        dependent.setVersionSatisfied(installed.satisfies(dependency.getVersion()));
                        dependents.add(dependent);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
        return dependents;
    }

    private void saveModuleData(Module module) {
        Collection<Long> ids = fileReferencePropertiesService.getEntityIdsForPropertyValue("name", module.getName());
        if (!ids.isEmpty()) {
            try {
                Long id = ids.iterator().next();
                FileReference reference = fileReferenceRepository.getOne(id);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ModuleVM vmFromModule(Module module) {
        try {
            ModuleConfig config = getConfig(module);
            ModuleVM vm = new ModuleVM();
            BeanUtils.copyProperties(module, vm);
            BeanUtils.copyProperties(config, vm);
            if (vm.getBuildDate() == null) {
                vm.setBuildDate(vm.getBuildTime());
            }
            return vm;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ModuleConfig getConfig(Module module) throws Exception {
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
        return ModuleUtils.loadModuleConfig(inputStream, "module.yml");
    }
}
