package com.mattae.simal.modules.base.domain.repositories;

import com.mattae.simal.modules.base.domain.entities.Module;
import com.mattae.simal.modules.base.domain.entities.WebRemote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WebRemoteRepository extends JpaRepository<WebRemote, UUID> {

    List<WebRemote> findByModule(Module module);
}
