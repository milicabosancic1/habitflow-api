package com.habitflow.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "habits")
public class Habit {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    private String name;

    private String category;

    @Enumerated(EnumType.STRING)
    private HabitType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency_type")
    private FrequencyType frequencyType;

    @Column(name = "days_of_week")
    private String daysOfWeek;

    @Column(name = "target_count")
    private Integer targetCount;

    @Column(name = "reminder_time")
    private String reminderTime;

    @Column(name = "cue_text")
    private String cueText;

    @Column(name = "stacked_after_habit_id", length = 36)
    private String stackedAfterHabitId;

    @Column(name = "is_archived")
    private boolean archived;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "updated_at")
    private Long updatedAt;

    public Habit() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

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
