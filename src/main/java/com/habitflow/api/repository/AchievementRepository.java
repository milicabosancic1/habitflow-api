package com.habitflow.api.repository;

import com.habitflow.api.entity.Achievement;
import com.habitflow.api.entity.AchievementType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, String> {
    List<Achievement> findByUserId(String userId);
    Optional<Achievement> findByUserIdAndType(String userId, AchievementType type);
}
