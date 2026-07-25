package com.habitflow.api.security;

import org.springframework.security.core.context.SecurityContextHolder;

/** Pomoćna klasa za dohvatanje ID-a trenutno autentikovanog korisnika. */
public final class CurrentUser {
    private CurrentUser() {}

    public static String id() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("Nema autentikovanog korisnika");
        }
        return auth.getPrincipal().toString();
    }
}
