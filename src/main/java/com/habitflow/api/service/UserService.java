package com.habitflow.api.service;

import com.habitflow.api.dto.UpdateProfileRequest;
import com.habitflow.api.dto.UserDto;
import com.habitflow.api.entity.User;
import com.habitflow.api.exception.NotFoundException;
import com.habitflow.api.mapper.UserMapper;
import com.habitflow.api.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto getProfile(String userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Korisnik nije pronađen"));
        return userMapper.toDto(u);
    }

    public UserDto updateProfile(String userId, UpdateProfileRequest req) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Korisnik nije pronađen"));
        u.setDisplayName(req.getDisplayName());
        u.setIdentityStatement(req.getIdentityStatement());
        return userMapper.toDto(userRepository.save(u));
    }
}
