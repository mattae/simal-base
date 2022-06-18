package com.mattae.simal.modules.base.domain.repositories;

import com.mattae.simal.modules.base.domain.entities.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModuleRepository extends JpaRepository<Module, UUID> {

    Optional<Module> findByName(String name);

    List<Module> findByProcessConfigIsTrue();

    List<Module> findByUninstallIsTrue();

    List<Module> findByStartedIsTrue();

    List<Module> findByActiveIsTrueAndStartedIsTrue();

    List<Module> findByUninstallIsFalse();
}
