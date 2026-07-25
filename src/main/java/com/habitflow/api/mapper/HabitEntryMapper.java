package com.habitflow.api.mapper;

import com.habitflow.api.dto.HabitEntryDto;
import com.habitflow.api.entity.HabitEntry;
import org.springframework.stereotype.Component;

@Component
public class HabitEntryMapper {

    public HabitEntryDto toDto(HabitEntry e) {
        HabitEntryDto d = new HabitEntryDto();
        d.setId(e.getId());
        d.setHabitId(e.getHabitId());
        d.setDate(e.getDate());
        d.setStatus(e.getStatus());
        d.setValue(e.getValue());
        d.setUpdatedAt(e.getUpdatedAt());
        return d;
    }

    public void applyToEntity(HabitEntryDto d, HabitEntry e) {
        e.setId(d.getId());
        e.setHabitId(d.getHabitId());
        e.setDate(d.getDate());
        e.setStatus(d.getStatus());
        e.setValue(d.getValue());
        e.setUpdatedAt(d.getUpdatedAt());
    }
}
