package com.mattae.simal.modules.base.services.api;

import com.mattae.simal.modules.base.domain.entities.Organisation;

public interface AuthService {
    String username();

    Organisation.IdView organisation();
}
