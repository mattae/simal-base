package com.mattae.simal.modules.base.security.jwt;

import com.foreach.across.modules.user.business.UserProperties;
import com.foreach.across.modules.user.repositories.UserRepository;
import com.foreach.across.modules.user.services.UserPropertiesService;
import com.mattae.simal.modules.base.config.JwtProperties;
import com.mattae.simal.modules.base.domain.entities.RefreshToken;
import com.mattae.simal.modules.base.domain.repositories.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String ORG_ID = "org_id";
    private final JwtProperties jwtProperties;
    private Key key;
    private long tokenValidityInMilliseconds;
    private long tokenValidityInMillisecondsForRememberMe;
    private final UserPropertiesService userPropertiesService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getBase64Secret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidityInMilliseconds = 1000 * jwtProperties.getTokenValidityInSecs();
        this.tokenValidityInMillisecondsForRememberMe = 1000 * jwtProperties.getTokenValidityInSecsForRememberMe();
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }
        String username = authentication.getName();
        UUID organisationId = userRepository.findByUsername(username).map(user -> {
            UserProperties userProperties = userPropertiesService.getProperties(user.getId());
            return (UUID) userProperties.getValue("organisationId");
        }).orElse(null);

        return Jwts.builder()
            .setIssuedAt(new Date())
            .setSubject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .claim(ORG_ID, organisationId)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .compact();
    }

    public String getTokenFromUsername(String username) {
        String authorities = userRepository.findByUsername(username).map(user -> user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","))).orElse("");
        UUID organisationId = userRepository.findByUsername(username).map(user -> {
            UserProperties userProperties = userPropertiesService.getProperties(user.getId());
            return (UUID) userProperties.getValue("organisationId");
        }).orElse(null);

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);
        return Jwts.builder()
            .setIssuedAt(new Date())
            .setSubject(username)
            .claim(AUTHORITIES_KEY, authorities)
            .claim(ORG_ID, organisationId)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .compact();
    }

    public String createRefreshToken(String username) {
        RefreshToken refreshToken = new RefreshToken();
        com.foreach.across.modules.user.business.User user = userRepository.findByUsername(username).get();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtProperties.getRefreshTokenDurationMillis()));

        if (StringUtils.isBlank(user.getUsername())) {
            throw new IllegalArgumentException("Cannot create JWT Token without username");
        }
        Claims claims = Jwts.claims().setSubject(user.getUsername());

        String token = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(new Date(new Date().getTime() + jwtProperties.getRefreshTokenDurationMillis()))
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
        refreshToken.setToken(token);
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();

        Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public UUID getOrganisationId(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
        try {
            return UUID.fromString(claims.get(ORG_ID, String.class));
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (ExpiredJwtException e) {
            LOG.trace("JWT token expired.");
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            LOG.trace("Invalid JWT token trace.", e);
        }
        return false;
    }
}
