package com.habitflow.api.dto;

import com.habitflow.api.entity.AchievementType;
import jakarta.validation.constraints.NotNull;

public class AchievementDto {
    private String id;

    @NotNull(message = "Tip dostignuća je obavezan")
    private AchievementType type;

    private Long unlockedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public AchievementType getType() { return type; }
    public void setType(AchievementType type) { this.type = type; }
    public Long getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(Long unlockedAt) { this.unlockedAt = unlockedAt; }
}
