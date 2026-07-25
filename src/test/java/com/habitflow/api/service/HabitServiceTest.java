package com.habitflow.api.service;

import com.habitflow.api.dto.HabitDto;
import com.habitflow.api.entity.Habit;
import com.habitflow.api.exception.NotFoundException;
import com.habitflow.api.mapper.HabitMapper;
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
 * Proverava da servis ne dozvoljava korisniku da menja/briše tuđe navike
 * (vlasništvo se proverava u service sloju, ne u bazi).
 */
class HabitServiceTest {

    private HabitRepository habitRepository;
    private HabitService habitService;

    @BeforeEach
    void setUp() {
        habitRepository = mock(HabitRepository.class);
        habitService = new HabitService(habitRepository, new HabitMapper());
    }

    @Test
    void update_throwsNotFound_whenHabitBelongsToAnotherUser() {
        Habit h = new Habit();
        h.setId("habit-1");
        h.setUserId("owner-A");
        when(habitRepository.findById("habit-1")).thenReturn(Optional.of(h));

        HabitDto dto = new HabitDto();
        dto.setName("Tuđa izmena");

        assertThatThrownBy(() -> habitService.update("owner-B", "habit-1", dto))
                .isInstanceOf(NotFoundException.class);

        verify(habitRepository, never()).save(any());
    }

    @Test
    void delete_archivesHabit_whenCallerIsOwner() {
        Habit h = new Habit();
        h.setId("habit-1");
        h.setUserId("owner-A");
        h.setArchived(false);
        when(habitRepository.findById("habit-1")).thenReturn(Optional.of(h));
        when(habitRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        habitService.delete("owner-A", "habit-1");

        ArgumentCaptor<Habit> captor = ArgumentCaptor.forClass(Habit.class);
        verify(habitRepository).save(captor.capture());
        assertThat(captor.getValue().isArchived()).isTrue();
    }

    @Test
    void delete_throwsNotFound_whenCallerIsNotOwner() {
        Habit h = new Habit();
        h.setId("habit-1");
        h.setUserId("owner-A");
        when(habitRepository.findById("habit-1")).thenReturn(Optional.of(h));

        assertThatThrownBy(() -> habitService.delete("owner-B", "habit-1"))
                .isInstanceOf(NotFoundException.class);

        verify(habitRepository, never()).save(any());
    }
}
