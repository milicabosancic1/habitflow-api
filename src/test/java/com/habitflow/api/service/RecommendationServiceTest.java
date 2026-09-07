package com.habitflow.api.service;

import com.habitflow.api.dto.RecommendationDto;
import com.habitflow.api.entity.Recommendation;
import com.habitflow.api.entity.RecommendationType;
import com.habitflow.api.exception.NotFoundException;
import com.habitflow.api.mapper.RecommendationMapper;
import com.habitflow.api.repository.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Proverava da servis ne dozvoljava korisniku da odbaci (dismiss) tuđu preporuku
 * (vlasništvo se proverava u service sloju, ne u bazi).
 */
class RecommendationServiceTest {

    private RecommendationRepository repository;
    private RecommendationService service;

    @BeforeEach
    void setUp() {
        repository = mock(RecommendationRepository.class);
        service = new RecommendationService(repository, new RecommendationMapper());
    }

    private Recommendation ownedRecommendation(String id, String ownerId) {
        Recommendation r = new Recommendation();
        r.setId(id);
        r.setUserId(ownerId);
        r.setType(RecommendationType.WEEKLY_INSIGHT);
        r.setMessage("Test poruka");
        r.setDismissed(false);
        return r;
    }

    @Test
    void getActive_returnsMappedDtos_forGivenUser() {
        Recommendation r = ownedRecommendation("rec-1", "owner-A");
        when(repository.findByUserIdAndDismissedFalse("owner-A")).thenReturn(List.of(r));

        List<RecommendationDto> result = service.getActive("owner-A");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("rec-1");
        assertThat(result.get(0).getMessage()).isEqualTo("Test poruka");
    }

    @Test
    void dismiss_throwsNotFound_whenRecommendationDoesNotExist() {
        when(repository.findById("rec-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.dismiss("owner-A", "rec-1"))
                .isInstanceOf(NotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void dismiss_throwsNotFound_whenRecommendationBelongsToAnotherUser() {
        when(repository.findById("rec-1")).thenReturn(Optional.of(ownedRecommendation("rec-1", "owner-A")));

        assertThatThrownBy(() -> service.dismiss("owner-B", "rec-1"))
                .isInstanceOf(NotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void dismiss_marksAsDismissed_whenCallerIsOwner() {
        Recommendation r = ownedRecommendation("rec-1", "owner-A");
        when(repository.findById("rec-1")).thenReturn(Optional.of(r));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.dismiss("owner-A", "rec-1");

        ArgumentCaptor<Recommendation> captor = ArgumentCaptor.forClass(Recommendation.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isDismissed()).isTrue();
    }
}
