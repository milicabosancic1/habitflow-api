package com.habitflow.api.repository;

import com.habitflow.api.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, String> {
    List<Habit> findByUserId(String userId);
    List<Habit> findByUserIdAndArchivedFalse(String userId);
    List<Habit> findByUserIdAndUpdatedAtGreaterThan(String userId, Long since);
}
