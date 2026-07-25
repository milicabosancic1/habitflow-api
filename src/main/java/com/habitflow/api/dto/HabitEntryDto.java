package com.habitflow.api.dto;

import com.habitflow.api.entity.EntryStatus;

public class HabitEntryDto {
    private String id;
    private String habitId;
    private String date;
    private EntryStatus status;
    private Integer value;
    private Long updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getHabitId() { return habitId; }
    public void setHabitId(String habitId) { this.habitId = habitId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public EntryStatus getStatus() { return status; }
    public void setStatus(EntryStatus status) { this.status = status; }
    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
