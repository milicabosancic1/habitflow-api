package com.habitflow.api.dto;

import com.habitflow.api.entity.EntryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

public class HabitEntryDto {
    private String id;

    @NotBlank(message = "habitId je obavezan")
    private String habitId;

    @NotBlank(message = "Datum je obavezan")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Datum mora biti u formatu YYYY-MM-DD")
    private String date;

    @NotNull(message = "Status je obavezan")
    private EntryStatus status;

    @NotNull(message = "Vrednost je obavezna")
    @PositiveOrZero(message = "Vrednost ne može biti negativna")
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
