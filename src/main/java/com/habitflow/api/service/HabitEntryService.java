package com.habitflow.api.service;

import com.habitflow.api.dto.HabitEntryDto;
import com.habitflow.api.entity.Habit;
import com.habitflow.api.entity.HabitEntry;
import com.habitflow.api.exception.NotFoundException;
import com.habitflow.api.mapper.HabitEntryMapper;
import com.habitflow.api.repository.HabitEntryRepository;
import com.habitflow.api.repository.HabitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HabitEntryService {

    private final HabitEntryRepository entryRepository;
    private final HabitRepository habitRepository;
    private final HabitEntryMapper entryMapper;

    public HabitEntryService(HabitEntryRepository entryRepository,
                             HabitRepository habitRepository,
                             HabitEntryMapper entryMapper) {
        this.entryRepository = entryRepository;
        this.habitRepository = habitRepository;
        this.entryMapper = entryMapper;
    }

    public List<HabitEntryDto> getEntries(String userId, String from, String to) {
        return entryRepository.findByUserInRange(userId, from, to).stream()
                .map(entryMapper::toDto)
                .collect(Collectors.toList());
    }

    /** Upsert po (habitId, date). */
    public HabitEntryDto record(String userId, HabitEntryDto dto) {
        Habit habit = habitRepository.findById(dto.getHabitId())
                .orElseThrow(() -> new NotFoundException("Navika nije pronađena"));
        if (!habit.getUserId().equals(userId)) {
            throw new NotFoundException("Navika nije pronađena");
        }

        HabitEntry entry = entryRepository
                .findByHabitIdAndDate(dto.getHabitId(), dto.getDate())
                .orElseGet(HabitEntry::new);

        if (entry.getId() == null) {
            entry.setId(dto.getId() != null && !dto.getId().isBlank()
                    ? dto.getId() : UUID.randomUUID().toString());
        }
        entry.setHabitId(dto.getHabitId());
        entry.setDate(dto.getDate());
        entry.setStatus(dto.getStatus());
        entry.setValue(dto.getValue());
        entry.setUpdatedAt(System.currentTimeMillis());

        return entryMapper.toDto(entryRepository.save(entry));
    }
}
