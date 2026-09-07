package com.habitflow.api.service;

import com.habitflow.api.dto.UpdateProfileRequest;
import com.habitflow.api.dto.UserDto;
import com.habitflow.api.entity.User;
import com.habitflow.api.exception.NotFoundException;
import com.habitflow.api.mapper.UserMapper;
import com.habitflow.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository, new UserMapper());
    }

    @Test
    void getProfile_throwsNotFound_whenUserDoesNotExist() {
        when(userRepository.findById("user-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfile("user-1"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getProfile_returnsMappedDto_whenUserExists() {
        User u = new User();
        u.setId("user-1");
        u.setEmail("test@example.com");
        u.setDisplayName("Test");
        when(userRepository.findById("user-1")).thenReturn(Optional.of(u));

        UserDto dto = userService.getProfile("user-1");

        assertThat(dto.getId()).isEqualTo("user-1");
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void updateProfile_throwsNotFound_whenUserDoesNotExist() {
        when(userRepository.findById("user-1")).thenReturn(Optional.empty());

        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setDisplayName("Novo ime");

        assertThatThrownBy(() -> userService.updateProfile("user-1", req))
                .isInstanceOf(NotFoundException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateProfile_updatesDisplayNameAndIdentityStatement() {
        User u = new User();
        u.setId("user-1");
        u.setDisplayName("Staro ime");
        when(userRepository.findById("user-1")).thenReturn(Optional.of(u));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setDisplayName("Novo ime");
        req.setIdentityStatement("Ja sam osoba koja trči");

        UserDto result = userService.updateProfile("user-1", req);

        assertThat(result.getDisplayName()).isEqualTo("Novo ime");
        assertThat(result.getIdentityStatement()).isEqualTo("Ja sam osoba koja trči");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getDisplayName()).isEqualTo("Novo ime");
    }
}
