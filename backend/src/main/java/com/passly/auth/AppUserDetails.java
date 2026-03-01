package com.passly.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

/**
 * Extends Spring's User to carry the UUID so controllers can get it
 * via @AuthenticationPrincipal without an extra DB query.
 */
@Getter
public class AppUserDetails extends User {

    private final UUID userId;

    public AppUserDetails(UUID userId,
                          String email,
                          String password,
                          boolean enabled,
                          Collection<? extends GrantedAuthority> authorities) {
        super(email, password, enabled, true, true, true, authorities);
        this.userId = userId;
    }
}
