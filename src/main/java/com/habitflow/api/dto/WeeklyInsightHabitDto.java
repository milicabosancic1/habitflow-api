package com.habitflow.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class WeeklyInsightHabitDto {

    @NotBlank(message = "Naziv navike je obavezan")
    private String habitName;

    private String category;

    @NotNull(message = "thisWeekPct je obavezan")
    private Integer thisWeekPct;

    @NotNull(message = "currentStreak je obavezan")
    private Integer currentStreak;

    public String getHabitName() { return habitName; }
    public void setHabitName(String habitName) { this.habitName = habitName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getThisWeekPct() { return thisWeekPct; }
    public void setThisWeekPct(Integer thisWeekPct) { this.thisWeekPct = thisWeekPct; }
    public Integer getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(Integer currentStreak) { this.currentStreak = currentStreak; }
}
