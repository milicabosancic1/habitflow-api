package com.habitflow.api.service;

import com.habitflow.api.entity.RefreshToken;
import com.habitflow.api.exception.UnauthorizedException;
import com.habitflow.api.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Refresh tokeni su opaque (nasumican string), ne JWT — cuvaju se u bazi
 * samo kao SHA-256 hash da bi mogli da se revokiraju (logout, rotacija).
 * Svaki login/register izdaje po jedan token = jedna sesija/uredjaj.
 */
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final long expirationMs;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository repository,
                                @Value("${app.jwt.refresh-expiration-ms}") long expirationMs) {
        this.repository = repository;
        this.expirationMs = expirationMs;
    }

    public String issue(String userId) {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        RefreshToken rt = new RefreshToken();
        rt.setId(UUID.randomUUID().toString());
        rt.setUserId(userId);
        rt.setTokenHash(hash(rawToken));
        long now = System.currentTimeMillis();
        rt.setCreatedAt(now);
        rt.setExpiresAt(now + expirationMs);
        rt.setRevoked(false);
        repository.save(rt);

        return rawToken;
    }

    public RefreshToken validate(String rawToken) {
        RefreshToken rt = repository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new UnauthorizedException("Nevažeći refresh token"));
        if (rt.isRevoked() || rt.getExpiresAt() < System.currentTimeMillis()) {
            throw new UnauthorizedException("Refresh token je istekao ili je opozvan");
        }
        return rt;
    }

    /** Rotacija: stari token se odmah opoziva, izdaje se novi za istog korisnika. */
    public String rotate(RefreshToken old) {
        old.setRevoked(true);
        repository.save(old);
        return issue(old.getUserId());
    }

    /** Logout — opoziva token. Nema greške ako token ne postoji (idempotentno). */
    public void revoke(String rawToken) {
        repository.findByTokenHash(hash(rawToken)).ifPresent(rt -> {
            rt.setRevoked(true);
            repository.save(rt);
        });
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 nije dostupan", e);
        }
    }
}
