package com.habitflow.api.service;

import com.habitflow.api.dto.HabitDto;
import com.habitflow.api.entity.Habit;
import com.habitflow.api.exception.NotFoundException;
import com.habitflow.api.mapper.HabitMapper;
import com.habitflow.api.repository.HabitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitMapper habitMapper;

    public HabitService(HabitRepository habitRepository, HabitMapper habitMapper) {
        this.habitRepository = habitRepository;
        this.habitMapper = habitMapper;
    }

    public List<HabitDto> getHabits(String userId) {
        return habitRepository.findByUserIdAndArchivedFalse(userId).stream()
                .map(habitMapper::toDto)
                .collect(Collectors.toList());
    }

    public HabitDto create(String userId, HabitDto dto) {
        Habit h = new Habit();
        if (dto.getId() == null || dto.getId().isBlank()) {
            dto.setId(UUID.randomUUID().toString());
        }
        long now = System.currentTimeMillis();
        if (dto.getCreatedAt() == null) dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        habitMapper.applyToEntity(dto, h);
        h.setUserId(userId);
        return habitMapper.toDto(habitRepository.save(h));
    }

    public HabitDto update(String userId, String id, HabitDto dto) {
        Habit h = habitRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Navika nije pronađena"));
        ensureOwner(h, userId);
        dto.setId(id);
        dto.setUpdatedAt(System.currentTimeMillis());
        habitMapper.applyToEntity(dto, h);
        h.setUserId(userId);
        return habitMapper.toDto(habitRepository.save(h));
    }

    public void delete(String userId, String id) {
        Habit h = habitRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Navika nije pronađena"));
        ensureOwner(h, userId);
        // Arhiviranje umesto trajnog brisanja (čuva istoriju)
        h.setArchived(true);
        h.setUpdatedAt(System.currentTimeMillis());
        habitRepository.save(h);
    }

    private void ensureOwner(Habit h, String userId) {
        if (!h.getUserId().equals(userId)) {
            throw new NotFoundException("Navika nije pronađena");
        }
    }
}
