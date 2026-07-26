package com.habitflow.api.service;

import com.habitflow.api.dto.AuthResponse;
import com.habitflow.api.dto.LoginRequest;
import com.habitflow.api.dto.RegisterRequest;
import com.habitflow.api.entity.RefreshToken;
import com.habitflow.api.entity.User;
import com.habitflow.api.exception.BadRequestException;
import com.habitflow.api.exception.UnauthorizedException;
import com.habitflow.api.repository.UserRepository;
import com.habitflow.api.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email je već registrovan");
        }
        User u = new User();
        u.setId(UUID.randomUUID().toString());
        u.setEmail(req.getEmail());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setDisplayName(req.getDisplayName());
        u.setIdentityStatement(req.getIdentityStatement());
        u.setCreatedAt(System.currentTimeMillis());
        userRepository.save(u);

        return issueTokens(u);
    }

    public AuthResponse login(LoginRequest req) {
        User u = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Pogrešan email ili lozinka"));
        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new UnauthorizedException("Pogrešan email ili lozinka");
        }
        return issueTokens(u);
    }

    /** Zamenjuje refresh token novim parom (access + refresh), stari refresh token se opoziva (rotacija). */
    public AuthResponse refresh(String rawRefreshToken) {
        RefreshToken rt = refreshTokenService.validate(rawRefreshToken);
        User u = userRepository.findById(rt.getUserId())
                .orElseThrow(() -> new UnauthorizedException("Korisnik ne postoji više"));
        String newRefreshToken = refreshTokenService.rotate(rt);
        String accessToken = jwtService.generateToken(u.getId());
        return new AuthResponse(u.getId(), accessToken, newRefreshToken, u.getDisplayName());
    }

    /** Logout — opoziva samo refresh token prosleđenog uređaja/sesije. */
    public void logout(String rawRefreshToken) {
        refreshTokenService.revoke(rawRefreshToken);
    }

    private AuthResponse issueTokens(User u) {
        String accessToken = jwtService.generateToken(u.getId());
        String refreshToken = refreshTokenService.issue(u.getId());
        return new AuthResponse(u.getId(), accessToken, refreshToken, u.getDisplayName());
    }
}
