package com.habitflow.api.service;

import com.habitflow.api.dto.AchievementDto;
import com.habitflow.api.entity.Achievement;
import com.habitflow.api.entity.AchievementType;
import com.habitflow.api.mapper.AchievementMapper;
import com.habitflow.api.repository.AchievementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * unlock() mora biti idempotentan po (userId, type) - Android strana ga zove kad god
 * lokalno otključa bedž, uključujući ponovljene pozive posle offline sinhronizacije,
 * pa ne sme da duplira niti prepiše originalni unlockedAt.
 */
class AchievementServiceTest {

    private AchievementRepository repository;
    private AchievementService service;

    @BeforeEach
    void setUp() {
        repository = mock(AchievementRepository.class);
        service = new AchievementService(repository, new AchievementMapper());
    }

    private AchievementDto unlockRequest(AchievementType type, Long unlockedAt) {
        AchievementDto dto = new AchievementDto();
        dto.setType(type);
        dto.setUnlockedAt(unlockedAt);
        return dto;
    }

    @Test
    void getAll_returnsMappedDtos_forGivenUser() {
        Achievement a = new Achievement();
        a.setId("ach-1");
        a.setUserId("user-1");
        a.setType(AchievementType.FIRST_HABIT);
        a.setUnlockedAt(1000L);
        when(repository.findByUserId("user-1")).thenReturn(List.of(a));

        List<AchievementDto> result = service.getAll("user-1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(AchievementType.FIRST_HABIT);
    }

    @Test
    void unlock_createsNewAchievement_whenNotAlreadyUnlocked() {
        when(repository.findByUserIdAndType("user-1", AchievementType.STREAK_7)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AchievementDto result = service.unlock("user-1", unlockRequest(AchievementType.STREAK_7, 5000L));

        assertThat(result.getId()).isNotBlank();
        assertThat(result.getType()).isEqualTo(AchievementType.STREAK_7);
        assertThat(result.getUnlockedAt()).isEqualTo(5000L);

        ArgumentCaptor<Achievement> captor = ArgumentCaptor.forClass(Achievement.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo("user-1");
    }

    @Test
    void unlock_usesServerTime_whenClientDidNotProvideUnlockedAt() {
        when(repository.findByUserIdAndType("user-1", AchievementType.STREAK_7)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        long before = System.currentTimeMillis();
        AchievementDto result = service.unlock("user-1", unlockRequest(AchievementType.STREAK_7, null));
        long after = System.currentTimeMillis();

        assertThat(result.getUnlockedAt()).isBetween(before, after);
    }

    @Test
    void unlock_returnsExisting_withoutSaving_whenAlreadyUnlocked() {
        Achievement existing = new Achievement();
        existing.setId("ach-1");
        existing.setUserId("user-1");
        existing.setType(AchievementType.STREAK_7);
        existing.setUnlockedAt(1000L);
        when(repository.findByUserIdAndType("user-1", AchievementType.STREAK_7)).thenReturn(Optional.of(existing));

        AchievementDto result = service.unlock("user-1", unlockRequest(AchievementType.STREAK_7, 9999L));

        assertThat(result.getId()).isEqualTo("ach-1");
        assertThat(result.getUnlockedAt()).isEqualTo(1000L);
        verify(repository, never()).save(any());
    }
}
