package com.habitflow.api.mapper;

import com.habitflow.api.dto.AchievementDto;
import com.habitflow.api.entity.Achievement;
import org.springframework.stereotype.Component;

@Component
public class AchievementMapper {

    public AchievementDto toDto(Achievement a) {
        AchievementDto d = new AchievementDto();
        d.setId(a.getId());
        d.setType(a.getType());
        d.setUnlockedAt(a.getUnlockedAt());
        return d;
    }
}
