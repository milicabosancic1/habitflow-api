package com.habitflow.api.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.habitflow.api.dto.LoginRequest;
import com.habitflow.api.entity.User;
import com.habitflow.api.exception.UnauthorizedException;
import com.habitflow.api.repository.UserRepository;
import com.habitflow.api.security.GoogleTokenVerifier;
import com.habitflow.api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AuthService.loginWithGoogle: kreiranje novog naloga, linkovanje postojeceg
 * (registrovanog preko email/lozinke) naloga, i odbijanje neverifikovanog email-a.
 * GoogleTokenVerifier je mockovan — ne kontaktira stvarni Google.
 */
class AuthServiceGoogleTest {

    private UserRepository userRepository;
    private RefreshTokenService refreshTokenService;
    private GoogleTokenVerifier googleTokenVerifier;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);
        refreshTokenService = mock(RefreshTokenService.class);
        googleTokenVerifier = mock(GoogleTokenVerifier.class);

        when(jwtService.generateToken(any())).thenReturn("access-token");
        when(refreshTokenService.issue(any())).thenReturn("refresh-token");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        authService = new AuthService(userRepository, passwordEncoder, jwtService,
                refreshTokenService, googleTokenVerifier);
    }

    private GoogleIdToken.Payload payload(String subject, String email, boolean verified, String name) {
        return new GoogleIdToken.Payload()
                .setSubject(subject)
                .setEmail(email)
                .setEmailVerified(verified)
                .set("name", name);
    }

    @Test
    void loginWithGoogle_createsNewUser_whenNoneExists() {
        when(googleTokenVerifier.verify("id-token")).thenReturn(payload("g-1", "nova@habitflow.dev", true, "Nova"));
        when(userRepository.findByGoogleId("g-1")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("nova@habitflow.dev")).thenReturn(Optional.empty());

        authService.loginWithGoogle("id-token");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("nova@habitflow.dev");
        assertThat(captor.getValue().getGoogleId()).isEqualTo("g-1");
        assertThat(captor.getValue().getDisplayName()).isEqualTo("Nova");
    }

    @Test
    void loginWithGoogle_linksExistingPasswordAccount_byEmail() {
        User existing = new User();
        existing.setId("user-1");
        existing.setEmail("stari@habitflow.dev");
        existing.setPasswordHash("$2a$10$nekihash");
        existing.setGoogleId(null);

        when(googleTokenVerifier.verify("id-token")).thenReturn(payload("g-2", "stari@habitflow.dev", true, "Stari"));
        when(userRepository.findByGoogleId("g-2")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("stari@habitflow.dev")).thenReturn(Optional.of(existing));

        authService.loginWithGoogle("id-token");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo("user-1");
        assertThat(captor.getValue().getGoogleId()).isEqualTo("g-2");
        assertThat(captor.getValue().getPasswordHash()).isEqualTo("$2a$10$nekihash"); // lozinka ostaje netaknuta
    }

    @Test
    void loginWithGoogle_reusesLinkedAccount_withoutRedundantSave() {
        User linked = new User();
        linked.setId("user-1");
        linked.setEmail("vec-linkovan@habitflow.dev");
        linked.setGoogleId("g-3");

        when(googleTokenVerifier.verify("id-token")).thenReturn(payload("g-3", "vec-linkovan@habitflow.dev", true, "Ime"));
        when(userRepository.findByGoogleId("g-3")).thenReturn(Optional.of(linked));

        authService.loginWithGoogle("id-token");

        verify(userRepository, never()).save(any());
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void loginWithGoogle_rejectsUnverifiedEmail() {
        when(googleTokenVerifier.verify("id-token")).thenReturn(payload("g-4", "nepotvrdjen@habitflow.dev", false, "X"));

        assertThatThrownBy(() -> authService.loginWithGoogle("id-token"))
                .isInstanceOf(UnauthorizedException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_forGoogleOnlyAccount_returns401InsteadOfCrashing() {
        User googleOnly = new User();
        googleOnly.setId("user-1");
        googleOnly.setEmail("google-only@habitflow.dev");
        googleOnly.setPasswordHash(null);
        when(userRepository.findByEmail("google-only@habitflow.dev")).thenReturn(Optional.of(googleOnly));

        LoginRequest req = new LoginRequest();
        req.setEmail("google-only@habitflow.dev");
        req.setPassword("bilokoja");

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(UnauthorizedException.class);
    }
}
