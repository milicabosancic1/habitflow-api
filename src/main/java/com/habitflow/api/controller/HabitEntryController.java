package com.habitflow.api.controller;

import com.habitflow.api.dto.HabitEntryDto;
import com.habitflow.api.security.CurrentUser;
import com.habitflow.api.service.HabitEntryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entries")
public class HabitEntryController {

    private final HabitEntryService entryService;

    public HabitEntryController(HabitEntryService entryService) {
        this.entryService = entryService;
    }

    @GetMapping
    public List<HabitEntryDto> getEntries(@RequestParam String from,
                                          @RequestParam String to) {
        return entryService.getEntries(CurrentUser.id(), from, to);
    }

    @PostMapping
    public HabitEntryDto record(@Valid @RequestBody HabitEntryDto dto) {
        return entryService.record(CurrentUser.id(), dto);
    }
}
