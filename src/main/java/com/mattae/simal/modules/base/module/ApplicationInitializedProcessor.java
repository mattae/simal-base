package com.mattae.simal.modules.base.module;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.filemanager.business.reference.FileReferenceService;
import com.mattae.simal.modules.base.domain.repositories.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ApplicationInitializedProcessor {
    private final ModuleConfigProcessor configProcessor;
    private final ModuleRepository moduleRepository;
    private final AcrossContext acrossContext;
    private final FileReferenceService fileReferenceService;
    private final TransactionTemplate transactionTemplate;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        processConfigurations();
        processUninstall();
        updateStartedModules();
    }

    private void updateStartedModules() {
        acrossContext.getModules().stream()
            .flatMap(acrossModule -> moduleRepository.findByName(acrossModule.getName()).stream())
            .forEach(module -> {
                module.setStarted(true);
                moduleRepository.save(module);
            });
    }

    private void processConfigurations() {
        moduleRepository.findByProcessConfigIsTrue()
            .forEach(module -> {
                module.setProcessConfig(false);
                transactionTemplate.execute(status -> {
                    configProcessor.deleteRelatedResources(module);
                    return null;
                });
                try {
                    configProcessor.processConfig(module);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                moduleRepository.save(module);
            });
    }

    private void processUninstall() {
        moduleRepository.findByUninstallIsTrue().forEach(module -> {
            if (module.getFile() != null) {
                fileReferenceService.delete(module.getFile(), true);
            }
            configProcessor.deleteRelatedResources(module);
            moduleRepository.delete(module);
        });
    }
}
