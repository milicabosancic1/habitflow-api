package com.habitflow.api.service;

import com.habitflow.api.dto.RecommendationDto;
import com.habitflow.api.entity.Recommendation;
import com.habitflow.api.exception.NotFoundException;
import com.habitflow.api.mapper.RecommendationMapper;
import com.habitflow.api.repository.RecommendationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servis za preporuke. U ovoj (backend) verziji vraća i upravlja preporukama.
 * Glavna rule-based logika generisanja je opisana u docs/recommendations.md
 * i primarno se izvršava na Androidu (offline). Server ih čuva/servira.
 */
@Service
public class RecommendationService {

    private final RecommendationRepository repository;
    private final RecommendationMapper mapper;

    public RecommendationService(RecommendationRepository repository,
                                 RecommendationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<RecommendationDto> getActive(String userId) {
        return repository.findByUserIdAndDismissedFalse(userId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public void dismiss(String userId, String id) {
        Recommendation r = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Preporuka nije pronađena"));
        if (!r.getUserId().equals(userId)) {
            throw new NotFoundException("Preporuka nije pronađena");
        }
        r.setDismissed(true);
        repository.save(r);
    }
}
