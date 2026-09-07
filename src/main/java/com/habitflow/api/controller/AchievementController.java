package com.habitflow.api.controller;

import com.habitflow.api.dto.AchievementDto;
import com.habitflow.api.security.CurrentUser;
import com.habitflow.api.service.AchievementService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService service;

    public AchievementController(AchievementService service) {
        this.service = service;
    }

    @GetMapping
    public List<AchievementDto> getAll() {
        return service.getAll(CurrentUser.id());
    }

    @PostMapping
    public AchievementDto unlock(@Valid @RequestBody AchievementDto dto) {
        return service.unlock(CurrentUser.id(), dto);
    }
}
