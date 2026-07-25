package com.habitflow.api.controller;

import com.habitflow.api.dto.HabitDto;
import com.habitflow.api.security.CurrentUser;
import com.habitflow.api.service.HabitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @GetMapping
    public List<HabitDto> getHabits() {
        return habitService.getHabits(CurrentUser.id());
    }

    @PostMapping
    public ResponseEntity<HabitDto> create(@RequestBody HabitDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(habitService.create(CurrentUser.id(), dto));
    }

    @PutMapping("/{id}")
    public HabitDto update(@PathVariable String id, @RequestBody HabitDto dto) {
        return habitService.update(CurrentUser.id(), id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        habitService.delete(CurrentUser.id(), id);
        return ResponseEntity.noContent().build();
    }
}
