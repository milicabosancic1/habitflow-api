package com.habitflow.api.service;

import com.habitflow.api.dto.AuthResponse;
import com.habitflow.api.dto.LoginRequest;
import com.habitflow.api.dto.RegisterRequest;
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

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

        String token = jwtService.generateToken(u.getId());
        return new AuthResponse(u.getId(), token, u.getDisplayName());
    }

    public AuthResponse login(LoginRequest req) {
        User u = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Pogrešan email ili lozinka"));
        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new UnauthorizedException("Pogrešan email ili lozinka");
        }
        String token = jwtService.generateToken(u.getId());
        return new AuthResponse(u.getId(), token, u.getDisplayName());
    }
}
