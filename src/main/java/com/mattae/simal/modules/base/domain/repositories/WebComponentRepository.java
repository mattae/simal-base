package com.mattae.simal.modules.base.domain.repositories;

import com.mattae.simal.modules.base.domain.entities.Module;
import com.mattae.simal.modules.base.domain.entities.WebComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebComponentRepository extends JpaRepository<WebComponent, String> {
    List<WebComponent> findByType(String type);

    List<WebComponent> findByModule(Module module);
}
