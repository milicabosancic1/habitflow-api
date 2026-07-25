package com.habitflow.api.repository;

import com.habitflow.api.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AchievementRepository extends JpaRepository<Achievement, String> {
    List<Achievement> findByUserId(String userId);
}
