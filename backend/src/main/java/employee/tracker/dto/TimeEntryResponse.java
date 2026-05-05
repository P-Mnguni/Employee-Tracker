package employee.tracker.dto;

import employee.tracker.model.TimeEntryStatus;
import java.time.LocalDateTime;

/**
 * TimeEntryResponse DTO - Represents what the API returns for time entry data
 * 
 * Flat, simple structure with no nested objects to prevent circular references.
 * This is the API contract between backend and frontend.
 * 
 * Example response:
 * {
 *      "id": 1,
 *      "clockInTime": "2026-04-29T08:00:00",
 *      "clockOutTime": "2026-04-29T17:00:00",
 *      "status": "APPROVED",
 *      "employeeId": 1
 * }
 */
public class TimeEntryResponse {
    
    /**
     * Unique identifier of the time entry
     */
    private Long id;

    /**
     * When the employee clocked in
     */
    private LocalDateTime clockInTime;

    /**
     * When the employee clocked out
     */
    private LocalDateTime clockOutTime;

    /**
     * Current status of the time entry (PENDING, APPROVED, REJECTED)
     */
    private TimeEntryStatus status;

    /**
     * ID of the employee who made this time entry
     * Using ID only prevents circular references
     */
    private Long employeeId;

    /**
     * Total hours worked
     * Can be null if clockOutTime is null
     */
    private Double totalHours;

    public TimeEntryResponse() {}

    public TimeEntryResponse(
        Long id, 
        LocalDateTime clockInTime, 
        LocalDateTime clockOutTime, 
        TimeEntryStatus status, 
        Long employeeId
    ) {
        this.id = id;
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
        this.status = status;
        this.employeeId = employeeId;
    }

    public TimeEntryResponse(
        Long id,
        LocalDateTime clockInTime,
        LocalDateTime clockOutTime,
        TimeEntryStatus status,
        Long employeeId,
        Double totalHours
    ) {
        this(id, clockInTime, clockOutTime, status, employeeId);
        this.totalHours = totalHours;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(LocalDateTime clockInTime) {
        this.clockInTime = clockInTime;
    }

    public LocalDateTime getClockOutTime() {
        return clockOutTime;
    }

    public void setClockOutTime(LocalDateTime clockOutTime) {
        this.clockOutTime = clockOutTime;
    }

    public TimeEntryStatus getStatus() {
        return status;
    }

    public void setStatus(TimeEntryStatus status) {
        this.status = status;
    }
    
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }

    // Helper Methods

    /**
     * Calculate total hours worked (if clocked out)
     */
    public void calculateTotalHours() {
        if (clockInTime != null && clockOutTime != null) {
            this.totalHours = java.time.Duration.between(clockInTime, clockOutTime).toMinutes() / 60.0;
        } else {
            this.totalHours = null;
        }
    }

    // toString() for debugging

    @Override
    public String toString() {
        return "TimeEntryResponse{" +
                "id=" + 
                ", clockInTime=" + clockInTime +
                ", clockOutTime=" + clockOutTime +
                ", status=" + status +
                ", employeeId" + employeeId +
                ", totalHours" + totalHours +
                '}';
    }
}
