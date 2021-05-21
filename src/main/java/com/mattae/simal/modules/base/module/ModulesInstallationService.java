package com.mattae.simal.modules.base.module;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.filemanager.business.reference.FileReferenceService;
import com.mattae.simal.modules.base.domain.entities.Module;
import com.mattae.simal.modules.base.domain.repositories.ModuleRepository;
import com.mattae.simal.modules.base.yml.ModuleConfig;
import com.mattae.simal.modules.base.configurer.DynamicModuleImportConfigurer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModulesInstallationService {
    private final ModuleConfigProcessor configProcessor;
    private final ModuleRepository moduleRepository;
    private final AcrossContext acrossContext;
    private final FileReferenceService fileReferenceService;

    @EventListener
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        moduleRepository.findByProcessConfigIsTrue()
            .forEach(module -> {
                String path = module.getFile() != null ? module.getFile().getFileDescriptor().getUri() : module.getName();
                final Path moduleRuntimePath = Paths.get(DynamicModuleImportConfigurer.MODULE_PATH, "runtime",
                    StringUtils.replace(path, "\\", "/").replaceAll(":", "/"));
                ModuleConfig config = null;
                try {
                    config = ModuleUtils.loadModuleConfig(new FileInputStream(moduleRuntimePath.toFile()), "module.yml");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (config != null) {
                    module.setProcessConfig(false);
                    configProcessor.deleteRelationships(module);
                    configProcessor.processConfig(config, module);
                }
            });
        List<Module> uninstall = moduleRepository.findByUninstallIsTrue();
        uninstall.forEach(this::deleteModule);

        acrossContext.getModules().stream()
            .flatMap(acrossModule -> moduleRepository.findByName(acrossModule.getName()).stream())
            .forEach(module -> {
                module.setStarted(true);
                moduleRepository.save(module);
            });
    }

    public void deleteModule(Module module) {
        if (module.getFile() != null) {
            fileReferenceService.delete(module.getFile(), true);
        }
        configProcessor.deleteRolesAndPermissions(module);
        moduleRepository.delete(module);
    }
}
