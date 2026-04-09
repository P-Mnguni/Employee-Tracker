package employee.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "break_periods")
public class BreakPeriod {
    
    // Primary
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationship to Shift (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    // Time Range
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // Break Type (for future use)
    @Enumerated(EnumType.STRING)
    @Column(name = "break_type")
    private BreakType breakType;                            // LUNCH, SHORT_BREAK, PAID_BREAK, etc.

    // Notes
    @Column(length = 500)
    private String notes;

    // Timestamps (Future-proofing)
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public BreakPeriod() {}

    public BreakPeriod(Shift shift, LocalTime startTime, LocalTime endTime) {
        this.shift = shift;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public BreakPeriod(Shift shift, LocalTime startTime, LocalTime endTime, BreakType breakType) {
        this(shift, startTime, endTime);
        this.breakType = breakType;
    }

    public BreakPeriod(Shift shift, LocalTime startTime, LocalTime endTime, String notes) {
        this(shift, startTime, endTime);
        this.notes = notes;
    }

    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper Methods

    /**
     * Check if the break time range is valid (start before end)
     */
    public boolean isValidTimeRange() {
        return startTime != null && endTime != null && !endTime.isBefore(startTime);
    }

    /**
     * Get the duration of the break in minutes
     * Useful for calculating total break time
     */
    public long getDurationMinutes() {
        if (startTime == null || endTime == null) return 0;

        if (isOvernightBreak()) {
            return (24 - startTime.getHour()) * 60 + endTime.getHour() * 60;
        } else {
            return java.time.Duration.between(startTime, endTime).toMinutes();
        }
    }

    /**
     * Get the duration of the break in hours
     */
    public double getDurationHours() {
        return getDurationMinutes() / 60.0;
    }

    /**
     * Check if this is an overnight break (end time is before start time)
     */
    public boolean isOvernightBreak() {
        return endTime.isBefore(startTime);
    }

    /**
     * Check if this break falls with in the shift's time range
     * Note: This requires the shift to be loaded
     */
    public boolean isWithinShift() {
        if (shift == null || shift.getStartTime() == null || shift.getEndTime() == null) {
            return false;
        }

        if (shift.isOvernightShift()) {
            return !startTime.isBefore(shift.getStartTime()) || !endTime.isAfter(shift.getEndTime());
        } else {
            return !startTime.isBefore(shift.getStartTime()) && !endTime.isAfter(shift.getEndTime());
        }
    }

    /**
     * Get user-friendly break time display
     */
    public String getBreakDisplay() {
        return String.format("%s - %s(%d min)",
                            startTime.toString(),
                            endTime.toString(),
                            getDurationMinutes());
    }

    /**
     * Check if break overlaps with another break
     */
    public boolean overlapsWith(BreakPeriod other) {
        if (other == null) return false;

        return (startTime.isBefore(other.getEndTime()) && endTime.isAfter(other.getStartTime()));
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public BreakType getBreakType() {
        return breakType;
    }

    public void setBreakType(BreakType breakType) {
        this.breakType = breakType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // toString() for debugging
    @Override
    public String toString() {
        return "BreakPeriod{" +
                "id=" + id +
                ", shiftId=" + (shift != null ? shift.getId() : "null") +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", breakType=" + breakType +
                ", durationMinutes=" + getDurationMinutes() +
                '}';
    }
}
