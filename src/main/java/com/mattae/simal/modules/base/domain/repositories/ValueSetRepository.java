package com.mattae.simal.modules.base.domain.repositories;

import com.mattae.simal.modules.base.domain.entities.Module;
import com.mattae.simal.modules.base.domain.entities.ValueSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface ValueSetRepository extends JpaRepository<ValueSet, Long> {
    @Modifying
    void deleteByModule(Module module);
}
