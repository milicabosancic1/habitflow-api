package com.habitflow.api.service;

import com.habitflow.api.dto.HabitDto;
import com.habitflow.api.dto.HabitEntryDto;
import com.habitflow.api.dto.SyncRequest;
import com.habitflow.api.entity.Habit;
import com.habitflow.api.entity.TrackingType;
import com.habitflow.api.mapper.HabitEntryMapper;
import com.habitflow.api.mapper.HabitMapper;
import com.habitflow.api.repository.HabitEntryRepository;
import com.habitflow.api.repository.HabitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Proverava srce sync mehanizma: last-write-wins po updatedAt i
 * da se ne primaju izmene za navike koje ne pripadaju korisniku.
 */
class SyncServiceTest {

    private HabitRepository habitRepository;
    private HabitEntryRepository entryRepository;
    private SyncService syncService;

    @BeforeEach
    void setUp() {
        habitRepository = mock(HabitRepository.class);
        entryRepository = mock(HabitEntryRepository.class);
        syncService = new SyncService(habitRepository, entryRepository, new HabitMapper(), new HabitEntryMapper());
        when(habitRepository.findByUserIdAndUpdatedAtGreaterThan(any(), any())).thenReturn(List.of());
        when(entryRepository.findByUserUpdatedSince(any(), any())).thenReturn(List.of());
        when(habitRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void incomingHabitNewerThanServer_overwritesServerVersion() {
        Habit existing = new Habit();
        existing.setId("h1");
        existing.setUserId("user-1");
        existing.setName("Staro ime");
        existing.setUpdatedAt(100L);
        when(habitRepository.findById("h1")).thenReturn(Optional.of(existing));

        HabitDto incoming = new HabitDto();
        incoming.setId("h1");
        incoming.setName("Novo ime");
        incoming.setUpdatedAt(200L);

        SyncRequest req = new SyncRequest();
        req.setSince(0L);
        req.setHabits(List.of(incoming));
        req.setEntries(List.of());

        syncService.sync("user-1", req);

        ArgumentCaptor<Habit> captor = ArgumentCaptor.forClass(Habit.class);
        verify(habitRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Novo ime");
    }

    @Test
    void incomingHabitOlderThanServer_isIgnored() {
        Habit existing = new Habit();
        existing.setId("h1");
        existing.setUserId("user-1");
        existing.setName("Serversko ime");
        existing.setUpdatedAt(200L);
        when(habitRepository.findById("h1")).thenReturn(Optional.of(existing));

        HabitDto incoming = new HabitDto();
        incoming.setId("h1");
        incoming.setName("Zastarela izmena");
        incoming.setUpdatedAt(100L);

        SyncRequest req = new SyncRequest();
        req.setSince(0L);
        req.setHabits(List.of(incoming));
        req.setEntries(List.of());

        syncService.sync("user-1", req);

        verify(habitRepository, never()).save(any());
    }

    @Test
    void newHabitWithTrackingFields_roundTripsThroughSync() {
        when(habitRepository.findById("h2")).thenReturn(Optional.empty());

        HabitDto incoming = new HabitDto();
        incoming.setId("h2");
        incoming.setName("Voda");
        incoming.setTrackingType(TrackingType.QUANTITY);
        incoming.setUnit("ml");
        incoming.setIncrementAmount(250);
        incoming.setUpdatedAt(100L);

        SyncRequest req = new SyncRequest();
        req.setSince(0L);
        req.setHabits(List.of(incoming));
        req.setEntries(List.of());

        syncService.sync("user-1", req);

        ArgumentCaptor<Habit> captor = ArgumentCaptor.forClass(Habit.class);
        verify(habitRepository).save(captor.capture());
        assertThat(captor.getValue().getTrackingType()).isEqualTo(TrackingType.QUANTITY);
        assertThat(captor.getValue().getUnit()).isEqualTo("ml");
        assertThat(captor.getValue().getIncrementAmount()).isEqualTo(250);
    }

    @Test
    void newHabitWithColorWeeklyTargetAndReplacementText_roundTripsThroughSync() {
        // Regresija: Android salje color/weeklyTarget/replacementText na sync, ali dok
        // backend nije znao za ova polja, tiho su se gubila na sledecem pull-u (server
        // ih nikad ne bi upamtio pa bi ih vratio kao null i prepisao lokalnu vrednost).
        when(habitRepository.findById("h4")).thenReturn(Optional.empty());

        HabitDto incoming = new HabitDto();
        incoming.setId("h4");
        incoming.setName("Ne pusi");
        incoming.setColor("#E8A87C");
        incoming.setWeeklyTarget(3);
        incoming.setReplacementText("Popij casu vode");
        incoming.setUpdatedAt(100L);

        SyncRequest req = new SyncRequest();
        req.setSince(0L);
        req.setHabits(List.of(incoming));
        req.setEntries(List.of());

        syncService.sync("user-1", req);

        ArgumentCaptor<Habit> captor = ArgumentCaptor.forClass(Habit.class);
        verify(habitRepository).save(captor.capture());
        assertThat(captor.getValue().getColor()).isEqualTo("#E8A87C");
        assertThat(captor.getValue().getWeeklyTarget()).isEqualTo(3);
        assertThat(captor.getValue().getReplacementText()).isEqualTo("Popij casu vode");
    }

    @Test
    void updateWithoutTrackingType_keepsExistingValue() {
        Habit existing = new Habit();
        existing.setId("h3");
        existing.setUserId("user-1");
        existing.setTrackingType(TrackingType.QUANTITY);
        existing.setUnit("ml");
        existing.setUpdatedAt(100L);
        when(habitRepository.findById("h3")).thenReturn(Optional.of(existing));

        HabitDto incoming = new HabitDto(); // stariji Android klijent — trackingType nije poslat
        incoming.setId("h3");
        incoming.setName("Novo ime");
        incoming.setUpdatedAt(200L);

        SyncRequest req = new SyncRequest();
        req.setSince(0L);
        req.setHabits(List.of(incoming));
        req.setEntries(List.of());

        syncService.sync("user-1", req);

        // trackingType je NOT NULL u bazi pa se stiti od gubitka vrednosti kad izostane iz DTO-a;
        // unit/incrementAmount su nullable i ponasaju se kao ostala opciona polja (pun replace).
        ArgumentCaptor<Habit> captor = ArgumentCaptor.forClass(Habit.class);
        verify(habitRepository).save(captor.capture());
        assertThat(captor.getValue().getTrackingType()).isEqualTo(TrackingType.QUANTITY);
    }

    @Test
    void entryForHabitOwnedByAnotherUser_isRejected() {
        Habit othersHabit = new Habit();
        othersHabit.setId("h1");
        othersHabit.setUserId("other-user");
        when(habitRepository.findById("h1")).thenReturn(Optional.of(othersHabit));

        HabitEntryDto entryDto = new HabitEntryDto();
        entryDto.setId("e1");
        entryDto.setHabitId("h1");
        entryDto.setDate("2026-07-25");
        entryDto.setUpdatedAt(100L);

        SyncRequest req = new SyncRequest();
        req.setSince(0L);
        req.setHabits(List.of());
        req.setEntries(List.of(entryDto));

        syncService.sync("user-1", req);

        verify(entryRepository, never()).save(any());
    }
}
