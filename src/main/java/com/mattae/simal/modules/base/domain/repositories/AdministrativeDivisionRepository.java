package com.mattae.simal.modules.base.domain.repositories;

import com.mattae.simal.modules.base.domain.entities.AdministrativeDivision;
import com.mattae.simal.modules.base.domain.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdministrativeDivisionRepository extends JpaRepository<AdministrativeDivision, Long> {

    List<AdministrativeDivision> findByCountryAndParentIsNull(Country country);

    List<AdministrativeDivision> findByParent(AdministrativeDivision administrativeDivision);
}
