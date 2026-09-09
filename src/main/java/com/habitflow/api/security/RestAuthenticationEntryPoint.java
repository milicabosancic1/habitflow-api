package com.habitflow.api.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Bez ovoga Spring Security default-uje na Http403ForbiddenEntryPoint za SVE auth
 * greške (token nedostaje, istekao ili je nevalidan) - a klijenti (npr. OkHttp
 * Authenticator na Androidu) osluškuju isključivo 401 da bi pokušali refresh
 * access tokena. Vraćen 403 tu bi tiho blokirao ceo refresh mehanizam čim istekne
 * access token, dok se korisnik ručno ne izloguje/uloguje.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
                "{\"timestamp\":%d,\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Nedostaje ili je nevažeći JWT token\"}",
                System.currentTimeMillis()));
    }
}
