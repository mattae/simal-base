package org.lamisplus.modules.base.domain.repositories;

import org.lamisplus.modules.base.domain.entities.LocalGovernment;
import org.lamisplus.modules.base.domain.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalGovernmentRepository extends JpaRepository<LocalGovernment, Long> {
    List<LocalGovernment> findByStateOrderByName(State state);

}
