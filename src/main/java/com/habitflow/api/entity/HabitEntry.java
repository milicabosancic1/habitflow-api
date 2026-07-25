package com.habitflow.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "habit_entries",
       uniqueConstraints = @UniqueConstraint(columnNames = {"habit_id", "date"}))
public class HabitEntry {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "habit_id", length = 36, nullable = false)
    private String habitId;

    @Column(name = "date", nullable = false)
    private String date; // YYYY-MM-DD

    @Enumerated(EnumType.STRING)
    private EntryStatus status;

    @Column(name = "`value`") // "value" je rezervisana reč u H2 (PostgreSQL mode) — kvotovano radi na oba dijalekta
    private Integer value;

    @Column(name = "updated_at")
    private Long updatedAt;

    public HabitEntry() {}

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
