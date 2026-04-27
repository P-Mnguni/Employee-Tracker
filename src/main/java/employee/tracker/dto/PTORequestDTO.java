package employee.tracker.dto;

import employee.tracker.model.LeaveType;
import java.time.LocalDate;

/**
 * PTORequestDTO - Data transfer object for PTO/Leave request API requests
 * 
 * This represents the data sent from the client when an employee requests 
 * time off. Bundles all request data int a clean, structured object.
 * 
 * Example JSON request:
 * {
 *      "employeeId": 1,
 *      "startDate": "2026-05-01",
 *      "endDate": "2026-05-03",
 *      "type": "PTO",
 *      "reason": "Personal leave"
 * }
 */
public class PTORequestDTO {

    // Core & Future-ready Fields
    
    private Long employeeId;

    private LocalDate startDate;

    private LocalDate endDate;

    private LeaveType type;

    private String reason;

    private Boolean isPartialDay;

    private Double daysRequested;

    private String notes;

    // === Constructors ===

    public PTORequestDTO() {}

    public PTORequestDTO(Long employeeId, LocalDate startDate, LocalDate endDate, LeaveType type, String reason) {
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.reason = reason;
    }

    public PTORequestDTO(
        Long employeeId,
        LocalDate startDate,
        LocalDate endDate,
        LeaveType type,
        String reason,
        Boolean isPartialDay,
        Double daysRequested
    ) {
        this(employeeId, startDate, endDate, type, reason);
        this.isPartialDay = isPartialDay;
        this.daysRequested = daysRequested;
    }

    public PTORequestDTO(
        Long employeeId,
        LocalDate startDate,
        LocalDate endDate,
        LeaveType type,
        String reason,
        Boolean isPartialDay,
        Double daysRequested,
        String notes
    ) {
        this(employeeId, startDate, endDate, type, reason, isPartialDay, daysRequested);
        this.notes = notes;
    }

    // === Getters and Setters ===

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LeaveType getType() {
        return type;
    }

    public void setType(LeaveType type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Boolean getIsPartialDay() {
        return isPartialDay;
    }

    public void setIsPartialDay(Boolean isPartialDay) {
        this.isPartialDay = isPartialDay;
    }

    public Double getDaysRequested() {
        return daysRequested;
    }

    public void setDaysRequested(Double daysRequested) {
        this.daysRequested = daysRequested;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // === Helper Methods ===

    /**
     * Validate that the request has all required fields and business rules are satisfied
     */
    public Boolean isValid() {
        if (employeeId == null || employeeId <= 0) return false;
        if (startDate == null || endDate == null) return false;
        if (type == null) return false;
        
        // Checks date range
        if (endDate.isBefore(startDate)) return false;

        // Checks if leave is in the past
        LocalDate today = LocalDate.now();
        if (startDate.isBefore(today)) return false;

        // Checks partial day validity
        if (isPartialDay != null && isPartialDay) {
            if (daysRequested == null || daysRequested <= 0 || daysRequested >= 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get the number of days requested
     * Accounts for partial day requests
     */
    public double getCalculatedDays() {
        if (isPartialDay != null && isPartialDay && daysRequested != null) {
            return daysRequested;
        }

        if (startDate == null || endDate == null) return 0;

        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    /**
     * Get user-friendly request summary
     */
    public String getRequestSummary() {
        return String.format(
            "%s - %s to %s(%s): %s", 
            type != null ? type : "Unknown",
            startDate != null ? startDate.toString() : "?",
            endDate != null ? endDate.toString() : "?",
            getCalculatedDays() + " days",
            reason != null ? reason : "No reason provided"
        );
    }

    // === toString() for debugging ===

    @Override
    public String toString() {
        return "PTORequestDTO{" +
                "employeeId=" + employeeId +
                ", startDate=" + startDate + 
                ", endDate=" + endDate +
                ", type=" + type +
                ", reason=" + reason +
                ", isPartialDay=" + isPartialDay +
                ", daysRequested=" + daysRequested +
                ", notes=" + notes + '\'' +
                '}';
    }
}