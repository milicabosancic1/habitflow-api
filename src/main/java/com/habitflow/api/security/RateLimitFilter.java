package com.habitflow.api.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Zastita od brute-force napada: ogranicava broj pokusaja na /api/auth/login
 * i /api/auth/register po IP adresi. In-memory (po instanci servera) —
 * dovoljno za jednu instancu; za vise instanci bi trebalo deljeno skladiste (npr. Redis).
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Namerno ograničeno na auth rute (brute-force meta). Ostatak API-ja (sync, habits,
    // entries...) zahteva važeći JWT pre nego što uopšte stigne do handlera, pa je rizik
    // od credential-stuffing/spam napada tu bitno manji — dodavanje throttling-a svuda je
    // van obima diplomskog rada, ali ostaje poznato ograničenje ako zatreba u produkciji.
    private static final Set<String> LIMITED_PATHS = Set.of(
            "/api/auth/login", "/api/auth/register", "/api/auth/google");

    private final int capacity;
    private final Duration refillPeriod;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimitFilter(
            @Value("${app.security.rate-limit.capacity:5}") int capacity,
            @Value("${app.security.rate-limit.refill-seconds:60}") long refillSeconds) {
        this.capacity = capacity;
        this.refillPeriod = Duration.ofSeconds(refillSeconds);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (!LIMITED_PATHS.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        Bucket bucket = buckets.computeIfAbsent(clientIp(request), ip -> newBucket());
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write(String.format(
                    "{\"timestamp\":%d,\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Previše pokušaja, pokušaj ponovo kasnije\"}",
                    System.currentTimeMillis()));
        }
    }

    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(capacity, refillPeriod)
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
