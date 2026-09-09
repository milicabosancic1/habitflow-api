package com.habitflow.api.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** 403 - korisnik JESTE autentikovan, ali nema pravo na traženu akciju (za razliku od 401). */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
                "{\"timestamp\":%d,\"status\":403,\"error\":\"Forbidden\",\"message\":\"Nemate dozvolu za ovu akciju\"}",
                System.currentTimeMillis()));
    }
}
