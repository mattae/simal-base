package org.lamisplus.modules.base.domain.repositories;

import org.lamisplus.modules.base.domain.entities.Module;
import org.lamisplus.modules.base.domain.entities.ModuleArtifact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModuleArtifactRepository extends JpaRepository<ModuleArtifact, Long> {
    Optional<ModuleArtifact> findByModule(Module module);
}
