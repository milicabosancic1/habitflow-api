package com.habitflow.api.mapper;

import com.habitflow.api.dto.RecommendationDto;
import com.habitflow.api.entity.Recommendation;
import org.springframework.stereotype.Component;

@Component
public class RecommendationMapper {

    public RecommendationDto toDto(Recommendation r) {
        RecommendationDto d = new RecommendationDto();
        d.setId(r.getId());
        d.setHabitId(r.getHabitId());
        d.setType(r.getType());
        d.setMessage(r.getMessage());
        d.setCreatedAt(r.getCreatedAt());
        d.setDismissed(r.isDismissed());
        return d;
    }
}
