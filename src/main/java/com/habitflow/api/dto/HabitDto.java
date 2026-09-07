package com.habitflow.api.dto;

import com.habitflow.api.entity.FrequencyType;
import com.habitflow.api.entity.HabitType;
import com.habitflow.api.entity.TrackingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class HabitDto {
    private String id;

    @NotBlank(message = "Naziv navike je obavezan")
    @Size(max = 255, message = "Naziv navike je predugačak")
    private String name;

    @Size(max = 255, message = "Kategorija je predugačka")
    private String category;

    @NotNull(message = "Tip navike je obavezan")
    private HabitType type;

    @NotNull(message = "Tip frekvencije je obavezan")
    private FrequencyType frequencyType;

    @Size(max = 50, message = "daysOfWeek je predugačak")
    private String daysOfWeek;

    @NotNull(message = "Ciljna vrednost je obavezna")
    @Positive(message = "Ciljna vrednost mora biti pozitivan broj")
    private Integer targetCount;

    private TrackingType trackingType; // null = zadrzi postojece/podrazumevano (SIMPLE)

    @Size(max = 50, message = "Jedinica je predugačka")
    private String unit;
    private Integer incrementAmount;

    @Size(max = 10, message = "reminderTime je predugačak")
    private String reminderTime;

    @Size(max = 500, message = "Cue tekst je predugačak")
    private String cueText;

    @Size(max = 36, message = "stackedAfterHabitId nije validan ID")
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
    public TrackingType getTrackingType() { return trackingType; }
    public void setTrackingType(TrackingType trackingType) { this.trackingType = trackingType; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Integer getIncrementAmount() { return incrementAmount; }
    public void setIncrementAmount(Integer incrementAmount) { this.incrementAmount = incrementAmount; }
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
