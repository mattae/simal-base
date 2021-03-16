package org.lamisplus.modules.base.domain.repositories;

import org.lamisplus.modules.base.domain.entities.Module;
import org.lamisplus.modules.base.domain.entities.WebRemote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebRemoteRepository extends JpaRepository<WebRemote, Long> {

    List<WebRemote> findByModule(Module module);
}
