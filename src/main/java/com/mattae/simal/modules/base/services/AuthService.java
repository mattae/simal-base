package com.mattae.simal.modules.base.services;

import com.mattae.simal.modules.base.domain.entities.Organisation;
import com.mattae.simal.modules.base.domain.repositories.OrganisationRepository;
import com.mattae.simal.modules.base.security.jwt.JWTFilter;
import com.mattae.simal.modules.base.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final HttpServletRequest servletRequest;
    private final TokenProvider tokenProvider;
    private final OrganisationRepository organisationRepository;

    public String username() {
        String jwt = JWTFilter.resolveToken(servletRequest);
        if (isValidToken(jwt)) {
            return tokenProvider.getAuthentication(jwt).getPrincipal().toString();
        }
        return null;
    }

    public Organisation organisation() {
        String jwt = JWTFilter.resolveToken(servletRequest);
        if (isValidToken(jwt)) {
            return organisationRepository.findById(tokenProvider.getOrganisationId(jwt)).orElse(null);
        }
        return null;
    }

    private boolean isValidToken(String jwt) {
        return StringUtils.hasText(jwt) && this.tokenProvider.validateToken(jwt);
    }
}
