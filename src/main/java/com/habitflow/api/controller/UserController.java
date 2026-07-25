package com.habitflow.api.controller;

import com.habitflow.api.dto.UpdateProfileRequest;
import com.habitflow.api.dto.UserDto;
import com.habitflow.api.security.CurrentUser;
import com.habitflow.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserDto me() {
        return userService.getProfile(CurrentUser.id());
    }

    @PutMapping("/me")
    public UserDto updateMe(@Valid @RequestBody UpdateProfileRequest req) {
        return userService.updateProfile(CurrentUser.id(), req);
    }
}
