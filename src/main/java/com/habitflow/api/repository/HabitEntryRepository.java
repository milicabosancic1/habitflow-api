package com.habitflow.api.repository;

import com.habitflow.api.entity.HabitEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HabitEntryRepository extends JpaRepository<HabitEntry, String> {

    Optional<HabitEntry> findByHabitIdAndDate(String habitId, String date);

    @Query("SELECT e FROM HabitEntry e WHERE e.habitId IN " +
           "(SELECT h.id FROM Habit h WHERE h.userId = :userId) " +
           "AND e.date BETWEEN :from AND :to")
    List<HabitEntry> findByUserInRange(@Param("userId") String userId,
                                       @Param("from") String from,
                                       @Param("to") String to);

    @Query("SELECT e FROM HabitEntry e WHERE e.habitId IN " +
           "(SELECT h.id FROM Habit h WHERE h.userId = :userId) " +
           "AND e.updatedAt > :since")
    List<HabitEntry> findByUserUpdatedSince(@Param("userId") String userId,
                                            @Param("since") Long since);
}
