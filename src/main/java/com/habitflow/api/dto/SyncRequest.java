package com.habitflow.api.dto;

import java.util.ArrayList;
import java.util.List;

public class SyncRequest {
    private Long since = 0L;
    private List<HabitDto> habits = new ArrayList<>();
    private List<HabitEntryDto> entries = new ArrayList<>();

    public Long getSince() { return since; }
    public void setSince(Long since) { this.since = since; }
    public List<HabitDto> getHabits() { return habits; }
    public void setHabits(List<HabitDto> habits) { this.habits = habits; }
    public List<HabitEntryDto> getEntries() { return entries; }
    public void setEntries(List<HabitEntryDto> entries) { this.entries = entries; }
}
