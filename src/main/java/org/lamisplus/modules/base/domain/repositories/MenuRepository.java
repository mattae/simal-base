package org.lamisplus.modules.base.domain.repositories;

import org.lamisplus.modules.base.domain.entities.Menu;
import org.lamisplus.modules.base.domain.entities.Module;
import org.lamisplus.modules.base.domain.enumeration.MenuLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByModuleAndLevel(Module module, MenuLevel level);

    List<Menu> findByLevelAndParentName(MenuLevel level, String name);

    List<Menu> findByModule(Module module);
}
