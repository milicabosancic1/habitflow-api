package com.habitflow.api.dto;

import com.habitflow.api.entity.FrequencyType;
import com.habitflow.api.entity.HabitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class HabitDto {
    private String id;

    @NotBlank(message = "Naziv navike je obavezan")
    private String name;

    private String category;

    @NotNull(message = "Tip navike je obavezan")
    private HabitType type;

    @NotNull(message = "Tip frekvencije je obavezan")
    private FrequencyType frequencyType;

    private String daysOfWeek;

    @NotNull(message = "Ciljna vrednost je obavezna")
    @Positive(message = "Ciljna vrednost mora biti pozitivan broj")
    private Integer targetCount;

    private String reminderTime;
    private String cueText;
    private String stackedAfterHabitId;
    private boolean archived;
    private Long createdAt;
    private Long updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public HabitType getType() { return type; }
    public void setType(HabitType type) { this.type = type; }
    public FrequencyType getFrequencyType() { return frequencyType; }
    public void setFrequencyType(FrequencyType frequencyType) { this.frequencyType = frequencyType; }
    public String getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(String daysOfWeek) { this.daysOfWeek = daysOfWeek; }
    public Integer getTargetCount() { return targetCount; }
    public void setTargetCount(Integer targetCount) { this.targetCount = targetCount; }
    public String getReminderTime() { return reminderTime; }
    public void setReminderTime(String reminderTime) { this.reminderTime = reminderTime; }
    public String getCueText() { return cueText; }
    public void setCueText(String cueText) { this.cueText = cueText; }
    public String getStackedAfterHabitId() { return stackedAfterHabitId; }
    public void setStackedAfterHabitId(String stackedAfterHabitId) { this.stackedAfterHabitId = stackedAfterHabitId; }
    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
