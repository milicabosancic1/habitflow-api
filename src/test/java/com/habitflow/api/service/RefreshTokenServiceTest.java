package com.habitflow.api.service;

import com.habitflow.api.entity.RefreshToken;
import com.habitflow.api.exception.UnauthorizedException;
import com.habitflow.api.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Refresh tokeni se čuvaju samo kao SHA-256 hash (nikad sirovi token u bazi), a
 * validate() mora odbiti opozvane i istekle tokene. Ovo je bezbednosno osetljiva
 * logika (login sesije), pa zaslužuje test i pored toga što je hash algoritam
 * privatna implementaciona sitnica.
 */
class RefreshTokenServiceTest {

    private static final long EXPIRATION_MS = 2_592_000_000L; // 30 dana

    private RefreshTokenRepository repository;
    private RefreshTokenService service;

    @BeforeEach
    void setUp() {
        repository = mock(RefreshTokenRepository.class);
        service = new RefreshTokenService(repository, EXPIRATION_MS);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void issue_storesHashedToken_notRawToken() {
        String rawToken = service.issue("user-1");

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(repository).save(captor.capture());
        RefreshToken saved = captor.getValue();

        assertThat(rawToken).isNotBlank();
        assertThat(saved.getUserId()).isEqualTo("user-1");
        assertThat(saved.getTokenHash()).isNotEqualTo(rawToken);
        assertThat(saved.isRevoked()).isFalse();
        assertThat(saved.getExpiresAt()).isGreaterThan(saved.getCreatedAt());
    }

    @Test
    void validate_throwsUnauthorized_whenTokenNotFound() {
        when(repository.findByTokenHash(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.validate("nepostojeci-token"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void validate_throwsUnauthorized_whenTokenRevoked() {
        RefreshToken rt = new RefreshToken();
        rt.setRevoked(true);
        rt.setExpiresAt(System.currentTimeMillis() + EXPIRATION_MS);
        when(repository.findByTokenHash(anyString())).thenReturn(Optional.of(rt));

        assertThatThrownBy(() -> service.validate("token"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void validate_throwsUnauthorized_whenTokenExpired() {
        RefreshToken rt = new RefreshToken();
        rt.setRevoked(false);
        rt.setExpiresAt(System.currentTimeMillis() - 1000);
        when(repository.findByTokenHash(anyString())).thenReturn(Optional.of(rt));

        assertThatThrownBy(() -> service.validate("token"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void validate_returnsToken_whenValidAndNotExpiredOrRevoked() {
        RefreshToken rt = new RefreshToken();
        rt.setUserId("user-1");
        rt.setRevoked(false);
        rt.setExpiresAt(System.currentTimeMillis() + EXPIRATION_MS);
        when(repository.findByTokenHash(anyString())).thenReturn(Optional.of(rt));

        RefreshToken result = service.validate("token");

        assertThat(result).isSameAs(rt);
    }

    @Test
    void rotate_revokesOldToken_andIssuesNewOneForSameUser() {
        RefreshToken old = new RefreshToken();
        old.setUserId("user-1");
        old.setRevoked(false);

        String newRawToken = service.rotate(old);

        assertThat(old.isRevoked()).isTrue();
        assertThat(newRawToken).isNotBlank();
        verify(repository, times(2)).save(any());
    }

    @Test
    void revoke_marksTokenAsRevoked_whenFound() {
        RefreshToken rt = new RefreshToken();
        rt.setRevoked(false);
        when(repository.findByTokenHash(anyString())).thenReturn(Optional.of(rt));

        service.revoke("token");

        assertThat(rt.isRevoked()).isTrue();
        verify(repository).save(rt);
    }

    @Test
    void revoke_isIdempotent_whenTokenDoesNotExist() {
        when(repository.findByTokenHash(anyString())).thenReturn(Optional.empty());

        assertThatCode(() -> service.revoke("nepostojeci-token")).doesNotThrowAnyException();
        verify(repository, never()).save(any());
    }
}
