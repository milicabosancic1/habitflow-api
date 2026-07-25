package com.habitflow.api.dto;

import com.habitflow.api.entity.RecommendationType;

public class RecommendationDto {
    private String id;
    private String habitId;
    private RecommendationType type;
    private String message;
    private Long createdAt;
    private boolean dismissed;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getHabitId() { return habitId; }
    public void setHabitId(String habitId) { this.habitId = habitId; }
    public RecommendationType getType() { return type; }
    public void setType(RecommendationType type) { this.type = type; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public boolean isDismissed() { return dismissed; }
    public void setDismissed(boolean dismissed) { this.dismissed = dismissed; }
}
