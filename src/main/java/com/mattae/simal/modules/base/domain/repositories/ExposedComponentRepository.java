package com.mattae.simal.modules.base.domain.repositories;

import com.mattae.simal.modules.base.domain.entities.ExposedComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExposedComponentRepository extends JpaRepository<ExposedComponent, Long> {
    Optional<ExposedComponent> findByUuid(String uuid);
}
