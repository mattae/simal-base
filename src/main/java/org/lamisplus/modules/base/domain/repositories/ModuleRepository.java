package org.lamisplus.modules.base.domain.repositories;

import org.lamisplus.modules.base.domain.entities.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module, Long> {

    Optional<Module> findByName(String name);

    List<Module> findByActiveIsTrueOrderByPriority();

    @Query("select m from Module m where m.active = true and m.inError = false")
    List<Module> findByActiveIsTrueAndInErrorIsFalse();

    @Query("select distinct m from Module m join m.webModules w where m.active = true and w.providesFor is not null")
    List<Module> findAllWithProviders();

    @Query("select distinct m from Module m join m.webModules w where m.active = true and w.providesFor is null")
    List<Module> findAllWithoutProviders();

    @Modifying
    @Query(value = "delete from form where module_id = ?1", nativeQuery = true)
    void deleteViewTemplates(Long moduleId);

    @Modifying
    @Query(value = "delete from menu where module_id = ?1", nativeQuery = true)
    void deleteMenus(Long moduleId);

    @Modifying
    @Query(value = "delete from menu where id = ?1", nativeQuery = true)
    void deleteMenu(Long menuId);

    @Modifying
    @Query(value = "delete from web_module where module_id = ?1", nativeQuery = true)
    void deleteWebModule(Long moduleId);

    @Modifying
    @Query(value = "delete from menu_authorities where menu_id = ?1", nativeQuery = true)
    void deleteMenuAuthorities(Long menuId);

    @Modifying
    @Query(value = "delete from authority where module_id = ?1", nativeQuery = true)
    void deleteAuthorities(Long moduleId);

    @Modifying
    @Query(value = "delete from module_dependencies where module_id = ?1", nativeQuery = true)
    void deleteDependency(Long moduleId);

    @Modifying
    @Query(value = "delete from module_artifact where module_id = ?1", nativeQuery = true)
    void deleteArtifact(Long moduleId);
}
