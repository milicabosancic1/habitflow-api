package com.habitflow.api.service;

import com.habitflow.api.dto.AchievementDto;
import com.habitflow.api.entity.Achievement;
import com.habitflow.api.mapper.AchievementMapper;
import com.habitflow.api.repository.AchievementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Bedževi se otključavaju rule-based logikom na Androidu (offline) i prijavljuju
 * serveru radi vidljivosti na više uređaja. unlock() je idempotentan po (userId,
 * type) - ako je korisnik već otključao taj tip (na bilo kom uređaju), zadržava
 * se prvobitni unlockedAt umesto da se prepiše.
 */
@Service
public class AchievementService {

    private final AchievementRepository repository;
    private final AchievementMapper mapper;

    public AchievementService(AchievementRepository repository, AchievementMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<AchievementDto> getAll(String userId) {
        return repository.findByUserId(userId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public AchievementDto unlock(String userId, AchievementDto dto) {
        return repository.findByUserIdAndType(userId, dto.getType())
                .map(mapper::toDto)
                .orElseGet(() -> {
                    Achievement a = new Achievement();
                    a.setId(UUID.randomUUID().toString());
                    a.setUserId(userId);
                    a.setType(dto.getType());
                    a.setUnlockedAt(dto.getUnlockedAt() != null ? dto.getUnlockedAt() : System.currentTimeMillis());
                    return mapper.toDto(repository.save(a));
                });
    }
}
