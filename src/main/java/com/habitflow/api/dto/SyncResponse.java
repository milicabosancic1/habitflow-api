package com.habitflow.api.dto;

import java.util.ArrayList;
import java.util.List;

public class SyncResponse {
    private Long serverTime;
    private List<HabitDto> habits = new ArrayList<>();
    private List<HabitEntryDto> entries = new ArrayList<>();

    public Long getServerTime() { return serverTime; }
    public void setServerTime(Long serverTime) { this.serverTime = serverTime; }
    public List<HabitDto> getHabits() { return habits; }
    public void setHabits(List<HabitDto> habits) { this.habits = habits; }
    public List<HabitEntryDto> getEntries() { return entries; }
    public void setEntries(List<HabitEntryDto> entries) { this.entries = entries; }
}
