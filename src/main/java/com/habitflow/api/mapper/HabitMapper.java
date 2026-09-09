package com.habitflow.api.mapper;

import com.habitflow.api.dto.HabitDto;
import com.habitflow.api.entity.Habit;
import org.springframework.stereotype.Component;

@Component
public class HabitMapper {

    public HabitDto toDto(Habit h) {
        HabitDto d = new HabitDto();
        d.setId(h.getId());
        d.setName(h.getName());
        d.setCategory(h.getCategory());
        d.setType(h.getType());
        d.setFrequencyType(h.getFrequencyType());
        d.setDaysOfWeek(h.getDaysOfWeek());
        d.setTargetCount(h.getTargetCount());
        d.setTrackingType(h.getTrackingType());
        d.setUnit(h.getUnit());
        d.setIncrementAmount(h.getIncrementAmount());
        d.setReminderTime(h.getReminderTime());
        d.setCueText(h.getCueText());
        d.setStackedAfterHabitId(h.getStackedAfterHabitId());
        d.setColor(h.getColor());
        d.setWeeklyTarget(h.getWeeklyTarget());
        d.setReplacementText(h.getReplacementText());
        d.setArchived(h.isArchived());
        d.setCreatedAt(h.getCreatedAt());
        d.setUpdatedAt(h.getUpdatedAt());
        return d;
    }

    /** Primeni polja iz DTO-a na entitet (userId se postavlja odvojeno). */
    public void applyToEntity(HabitDto d, Habit h) {
        h.setId(d.getId());
        h.setName(d.getName());
        h.setCategory(d.getCategory());
        h.setType(d.getType());
        h.setFrequencyType(d.getFrequencyType());
        h.setDaysOfWeek(d.getDaysOfWeek());
        h.setTargetCount(d.getTargetCount());
        if (d.getTrackingType() != null) h.setTrackingType(d.getTrackingType());
        h.setUnit(d.getUnit());
        h.setIncrementAmount(d.getIncrementAmount());
        h.setReminderTime(d.getReminderTime());
        h.setCueText(d.getCueText());
        h.setStackedAfterHabitId(d.getStackedAfterHabitId());
        h.setColor(d.getColor());
        h.setWeeklyTarget(d.getWeeklyTarget());
        h.setReplacementText(d.getReplacementText());
        h.setArchived(d.isArchived());
        if (d.getCreatedAt() != null) h.setCreatedAt(d.getCreatedAt());
        h.setUpdatedAt(d.getUpdatedAt());
    }
}
