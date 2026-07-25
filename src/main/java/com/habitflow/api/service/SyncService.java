package com.habitflow.api.service;

import com.habitflow.api.dto.*;
import com.habitflow.api.entity.Habit;
import com.habitflow.api.entity.HabitEntry;
import com.habitflow.api.mapper.HabitEntryMapper;
import com.habitflow.api.mapper.HabitMapper;
import com.habitflow.api.repository.HabitEntryRepository;
import com.habitflow.api.repository.HabitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Srce offline-first sinhronizacije.
 * Strategija rešavanja konflikata: LAST-WRITE-WINS po polju updatedAt.
 * Namerno jednostavno i objašnjivo (pogodno za odbranu diplomskog rada).
 */
@Service
public class SyncService {

    private final HabitRepository habitRepository;
    private final HabitEntryRepository entryRepository;
    private final HabitMapper habitMapper;
    private final HabitEntryMapper entryMapper;

    public SyncService(HabitRepository habitRepository,
                       HabitEntryRepository entryRepository,
                       HabitMapper habitMapper,
                       HabitEntryMapper entryMapper) {
        this.habitRepository = habitRepository;
        this.entryRepository = entryRepository;
        this.habitMapper = habitMapper;
        this.entryMapper = entryMapper;
    }

    @Transactional
    public SyncResponse sync(String userId, SyncRequest req) {
        long since = req.getSince() == null ? 0L : req.getSince();

        // 1) PUSH: primeni dolazne izmene sa klijenta (last-write-wins)
        for (HabitDto dto : req.getHabits()) {
            pushHabit(userId, dto);
        }
        for (HabitEntryDto dto : req.getEntries()) {
            pushEntry(userId, dto);
        }

        // 2) PULL: vrati sve što je na serveru novije od 'since'
        long serverTime = System.currentTimeMillis();
        SyncResponse resp = new SyncResponse();
        resp.setServerTime(serverTime);

        habitRepository.findByUserIdAndUpdatedAtGreaterThan(userId, since)
                .forEach(h -> resp.getHabits().add(habitMapper.toDto(h)));
        entryRepository.findByUserUpdatedSince(userId, since)
                .forEach(e -> resp.getEntries().add(entryMapper.toDto(e)));

        return resp;
    }

    private void pushHabit(String userId, HabitDto dto) {
        if (dto.getId() == null || dto.getUpdatedAt() == null) return;
        Habit existing = habitRepository.findById(dto.getId()).orElse(null);

        if (existing == null) {
            Habit h = new Habit();
            habitMapper.applyToEntity(dto, h);
            h.setUserId(userId);
            habitRepository.save(h);
        } else if (existing.getUserId().equals(userId)
                && dto.getUpdatedAt() > safe(existing.getUpdatedAt())) {
            // dolazni zapis je noviji -> primeni (last-write-wins)
            habitMapper.applyToEntity(dto, existing);
            existing.setUserId(userId);
            habitRepository.save(existing);
        }
        // inače: serverska verzija je novija ili jednaka -> zadrži je
    }

    private void pushEntry(String userId, HabitEntryDto dto) {
        if (dto.getId() == null || dto.getUpdatedAt() == null) return;
        // provera vlasništva preko navike
        Habit habit = habitRepository.findById(dto.getHabitId()).orElse(null);
        if (habit == null || !habit.getUserId().equals(userId)) return;

        HabitEntry existing = entryRepository.findById(dto.getId()).orElse(null);
        if (existing == null) {
            // proveri i po (habitId, date) da ne prekršimo unique
            existing = entryRepository
                    .findByHabitIdAndDate(dto.getHabitId(), dto.getDate())
                    .orElse(null);
        }

        if (existing == null) {
            HabitEntry e = new HabitEntry();
            entryMapper.applyToEntity(dto, e);
            entryRepository.save(e);
        } else if (dto.getUpdatedAt() > safe(existing.getUpdatedAt())) {
            entryMapper.applyToEntity(dto, existing);
            entryRepository.save(existing);
        }
    }

    private long safe(Long v) { return v == null ? 0L : v; }
}
