package org.lamisplus.modules.base.domain.repositories;

import org.lamisplus.modules.base.domain.entities.WebComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebComponentRepository extends JpaRepository<WebComponent, String> {
    List<WebComponent> findByType(String type);
}
