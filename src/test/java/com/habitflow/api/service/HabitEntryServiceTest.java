package com.habitflow.api.service;

import com.habitflow.api.dto.HabitEntryDto;
import com.habitflow.api.entity.EntryStatus;
import com.habitflow.api.entity.Habit;
import com.habitflow.api.entity.HabitEntry;
import com.habitflow.api.exception.NotFoundException;
import com.habitflow.api.mapper.HabitEntryMapper;
import com.habitflow.api.repository.HabitEntryRepository;
import com.habitflow.api.repository.HabitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Proverava da servis ne dozvoljava zapisivanje unosa na tuđu naviku (vlasništvo
 * se proverava preko habit.userId, entry sam po sebi nema userId) i da je
 * record() upsert po (habitId, date) kako je definisano u ../shared/api-spec.md.
 */
class HabitEntryServiceTest {

    private HabitEntryRepository entryRepository;
    private HabitRepository habitRepository;
    private HabitEntryService service;

    @BeforeEach
    void setUp() {
        entryRepository = mock(HabitEntryRepository.class);
        habitRepository = mock(HabitRepository.class);
        service = new HabitEntryService(entryRepository, habitRepository, new HabitEntryMapper());
    }

    private Habit ownedHabit(String id, String ownerId) {
        Habit h = new Habit();
        h.setId(id);
        h.setUserId(ownerId);
        return h;
    }

    private HabitEntryDto entryDto(String habitId, String date) {
        HabitEntryDto dto = new HabitEntryDto();
        dto.setHabitId(habitId);
        dto.setDate(date);
        dto.setStatus(EntryStatus.DONE);
        dto.setValue(1);
        return dto;
    }

    @Test
    void record_throwsNotFound_whenHabitDoesNotExist() {
        when(habitRepository.findById("habit-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.record("owner-A", entryDto("habit-1", "2026-08-31")))
                .isInstanceOf(NotFoundException.class);

        verify(entryRepository, never()).save(any());
    }

    @Test
    void record_throwsNotFound_whenHabitBelongsToAnotherUser() {
        when(habitRepository.findById("habit-1")).thenReturn(Optional.of(ownedHabit("habit-1", "owner-A")));

        assertThatThrownBy(() -> service.record("owner-B", entryDto("habit-1", "2026-08-31")))
                .isInstanceOf(NotFoundException.class);

        verify(entryRepository, never()).save(any());
    }

    @Test
    void record_createsNewEntry_whenNoneExistsForDate() {
        when(habitRepository.findById("habit-1")).thenReturn(Optional.of(ownedHabit("habit-1", "owner-A")));
        when(entryRepository.findByHabitIdAndDate("habit-1", "2026-08-31")).thenReturn(Optional.empty());
        when(entryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        HabitEntryDto result = service.record("owner-A", entryDto("habit-1", "2026-08-31"));

        assertThat(result.getId()).isNotBlank();
        ArgumentCaptor<HabitEntry> captor = ArgumentCaptor.forClass(HabitEntry.class);
        verify(entryRepository).save(captor.capture());
        assertThat(captor.getValue().getHabitId()).isEqualTo("habit-1");
        assertThat(captor.getValue().getDate()).isEqualTo("2026-08-31");
    }

    @Test
    void record_updatesExistingEntry_whenOneAlreadyExistsForDate() {
        HabitEntry existing = new HabitEntry();
        existing.setId("entry-1");
        existing.setHabitId("habit-1");
        existing.setDate("2026-08-31");
        existing.setStatus(EntryStatus.MISSED);
        existing.setValue(0);

        when(habitRepository.findById("habit-1")).thenReturn(Optional.of(ownedHabit("habit-1", "owner-A")));
        when(entryRepository.findByHabitIdAndDate("habit-1", "2026-08-31")).thenReturn(Optional.of(existing));
        when(entryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        HabitEntryDto updated = entryDto("habit-1", "2026-08-31");
        updated.setStatus(EntryStatus.DONE);
        updated.setValue(3);

        HabitEntryDto result = service.record("owner-A", updated);

        assertThat(result.getId()).isEqualTo("entry-1");
        assertThat(result.getStatus()).isEqualTo(EntryStatus.DONE);
        assertThat(result.getValue()).isEqualTo(3);

        ArgumentCaptor<HabitEntry> captor = ArgumentCaptor.forClass(HabitEntry.class);
        verify(entryRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo("entry-1");
    }
}
