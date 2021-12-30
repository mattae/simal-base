package com.mattae.simal.modules.base.services;

import com.blazebit.persistence.view.EntityViewManager;
import com.mattae.simal.modules.base.domain.entities.Organisation;
import com.mattae.simal.modules.base.domain.repositories.OrganisationRepository;
import com.mattae.simal.modules.base.security.jwt.JWTFilter;
import com.mattae.simal.modules.base.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final HttpServletRequest servletRequest;
    private final TokenProvider tokenProvider;
    private final OrganisationRepository organisationRepository;
    private final EntityViewManager evm;
    private final EntityManager em;

    public String username() {
        String jwt = JWTFilter.resolveToken(servletRequest);
        if (isValidToken(jwt)) {
            return tokenProvider.getAuthentication(jwt).getPrincipal().toString();
        }
        return null;
    }

    public Organisation.IdView organisation() {
        String jwt = JWTFilter.resolveToken(servletRequest);
        if (isValidToken(jwt)) {
            try {
                return evm.find(em, Organisation.IdView.class, tokenProvider.getOrganisationId(jwt));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private boolean isValidToken(String jwt) {
        return StringUtils.hasText(jwt) && this.tokenProvider.validateToken(jwt);
    }
}
