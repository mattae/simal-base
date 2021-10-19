package com.mattae.simal.modules.base.web.rest;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.foreach.across.modules.user.repositories.UserRepository;
import com.mattae.simal.modules.base.domain.entities.RefreshToken;
import com.mattae.simal.modules.base.security.RefreshTokenService;
import com.mattae.simal.modules.base.security.TokenRefreshException;
import com.mattae.simal.modules.base.security.jwt.JWTFilter;
import com.mattae.simal.modules.base.security.jwt.TokenProvider;
import com.mattae.simal.modules.base.web.vm.LoginVM;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserJWTController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;


    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());
        Authentication authentication = authenticationManagerBuilder
            .getOrBuild()
            .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        boolean rememberMe = loginVM.isRememberMe() != null && loginVM.isRememberMe();
        String jwt = tokenProvider.createToken(authentication, rememberMe);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new JWTToken(jwt, tokenProvider.createRefreshToken(loginVM.getUsername())), httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUser)
            .map(user -> {
                String token = tokenProvider.getTokenFromUsername(user.getUsername());
                return ResponseEntity.ok(new JWTToken(token, tokenProvider.createRefreshToken(user.getUsername())));
            })
            .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                "Refresh token is not in database!"));
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private final static String tokenType = "Bearer";
        private String accessToken;
        private String refreshToken;

        JWTToken(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        @JsonProperty("access_token")
        String getAccessToken() {
            return accessToken;
        }

        void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        @JsonProperty("refresh_token")
        String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        @JsonProperty("token_type")
        public String getTokenType() {
            return tokenType;
        }
    }

    static class TokenRefreshRequest {
        @NotBlank
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}
