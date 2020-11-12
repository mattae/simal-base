package org.lamisplus.modules.base.module;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entities.Module;
import org.lamisplus.modules.base.domain.entities.ModuleArtifact;
import org.lamisplus.modules.base.domain.repositories.MenuRepository;
import org.lamisplus.modules.base.domain.repositories.ModuleArtifactRepository;
import org.lamisplus.modules.base.domain.repositories.ModuleRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleDeleteService {
    private final ModuleRepository moduleRepository;
    private final ModuleArtifactRepository moduleArtifactRepository;
    private final MenuRepository menuRepository;
    private final TransactionTemplate transactionTemplate;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void deleteModule(Module module) {
        Optional<ModuleArtifact> artifact = moduleArtifactRepository.findByModule(module);
        artifact.ifPresent(moduleArtifactRepository::delete);
        transactionTemplate.execute(ts -> {
            menuRepository.findByModule(module).stream()
                    .sorted((m1, m2) -> m2.getId().compareTo(m1.getId()))
                    .forEach(m -> {
                        moduleRepository.deleteMenuAuthorities(m.getId());
                        moduleRepository.deleteMenu(m.getId());
                    });
            //moduleRepository.deleteMenus(module.getId());
            moduleRepository.deleteDependency(module.getId());
            moduleRepository.deleteWebModule(module.getId());
            moduleRepository.deleteAuthorities(module.getId());
            moduleRepository.deleteViewTemplates(module.getId());
            moduleRepository.deleteArtifact(module.getId());
            jdbcTemplate.update("delete from module where id = ?", module.getId());
            return null;
        });
    }
}
