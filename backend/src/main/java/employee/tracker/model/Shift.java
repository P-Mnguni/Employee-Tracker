package employee.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "shifts")
public class Shift {
    
    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationship to Employee (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // Core Fields - Date
    @Column(name = "shift_date", nullable = false)
    private LocalDate shiftDate;

    // Core Fields - Time Range
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // Shift Type
    @Enumerated(EnumType.STRING)
    @Column(name = "shift_type")
    private ShiftType shiftType;                            // MORNING, EVENING, NIGHT

    // Notes
    @Column(length = 500)
    private String notes;

    // Timestamps (Future-proofing)
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Shift() {}

    public Shift(Employee employee, LocalDate shiftDate, LocalTime startTime, LocalTime endTime) {
        this.employee = employee;
        this.shiftDate = shiftDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Shift(Employee employee, LocalDate shiftDate, LocalTime startTime, LocalTime endTime, ShiftType shiftType) {
        this(employee, shiftDate, startTime, endTime);
        this.shiftType = shiftType;
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
     * Check if the shift time range is valid (start before end)
     */
    public boolean isValidTimeRange() {
        return startTime != null && endTime != null && !endTime.isBefore(startTime);
    }

    /**
     * Check if this is an overnight shift (end time is before start time)
     * E.g.: Start at 10:00 PM, End at 6:00 AM next day
     */
    public boolean isOvernightShift() {
        return endTime.isBefore(startTime);
    }

    /**
     * Get the duration of the shift in hours
     * Note: This is planned duration, not actual worked time
     */
    public double getPlannedDurationHours() {
        if (startTime == null || endTime == null) {
            return 0.0;
        }

        if (isOvernightShift()) {
            return (24 - startTime.getHour()) + endTime.getHour();
        } else {
            return endTime.getHour() - startTime.getHour();
        }
    }

    /**
     * Check if a given time falls within this shift
     * Useful for clock-in validation
     */
    public boolean coversTime(LocalTime time) {
        if (time == null) return false;

        if (isOvernightShift()) {
            return !time.isBefore(startTime) || !time.isAfter(endTime);
        } else {
            return !time.isBefore(startTime) && !time.isAfter(endTime);
        }
    }

    /**
     * Get user-friendly shift time display
     */
    public String getShiftDisplay() {
        return String.format("%s %s - %s",
                            shiftDate.toString(),
                            startTime.toString(),
                            endTime.toString());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
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

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
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
        return "Shift{" +
                "id=" + id +
                ", employee=" + (employee != null ? employee.getName() : "null") +
                ", shiftDate=" + shiftDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", shiftType=" + shiftType +
                ", isOvernight=" + isOvernightShift() +
                '}'; 
    }
}
