package com.habitflow.api.mapper;

import com.habitflow.api.dto.UserDto;
import com.habitflow.api.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User u) {
        UserDto d = new UserDto();
        d.setId(u.getId());
        d.setEmail(u.getEmail());
        d.setDisplayName(u.getDisplayName());
        d.setIdentityStatement(u.getIdentityStatement());
        d.setCreatedAt(u.getCreatedAt());
        return d;
    }
}
