package com.habitflow.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class WeeklyInsightRequest {

    @NotNull(message = "thisWeekPct je obavezan")
    private Integer thisWeekPct;

    @NotNull(message = "lastWeekPct je obavezan")
    private Integer lastWeekPct;

    @NotNull(message = "habits je obavezan")
    @Valid
    private List<WeeklyInsightHabitDto> habits;

    public Integer getThisWeekPct() { return thisWeekPct; }
    public void setThisWeekPct(Integer thisWeekPct) { this.thisWeekPct = thisWeekPct; }
    public Integer getLastWeekPct() { return lastWeekPct; }
    public void setLastWeekPct(Integer lastWeekPct) { this.lastWeekPct = lastWeekPct; }
    public List<WeeklyInsightHabitDto> getHabits() { return habits; }
    public void setHabits(List<WeeklyInsightHabitDto> habits) { this.habits = habits; }
}
