package employee.tracker.dto;

import java.time.LocalDate;

/**
 * TimesheetRequest DTO - Data transfer object for timesheet submission API requests
 * 
 * This represents the data sent from the client when an employee submits a timesheet 
 * for a specific pay period. Groups employee reference and date range into a clean, 
 * structured object for API consumption.
 * 
 * Example JSON request:
 * {
 *      "employeeId": 1,
 *      "startDate": "2026-04-01",
 *      "endDate": "2026-04-07"
 * }
 */
public class TimesheetRequest {

    // === Core & Future-ready Fields

    /**
     * The ID of the employee submitting the timesheet
     * This is required and must correspond to an existing employee
     */
    private Long employeeId;

    /**
     * Start date of the pay period (inclusive)
     * Format: yyyy-MM-dd (ISO standard)
     */
    private LocalDate startDate;

    /**
     * End date of the pay period (inclusive)
     * Format: yyyy-MM-dd (ISO standard)
     */
    private LocalDate endDate;

    /**
     * Any additional notes from the employee about this timesheet
     * Example: "Worked on client project, had 2 overtime days"
     */
    private String notes;

    /**
     * Whether this is an amended/resubmitted timesheet
     */
    private Boolean isResubmission;

    /**
     * Previous timesheet ID if this is a resubmission
     */
    private Long previousTimesheetId;

    // === Constructors ===

    public TimesheetRequest() {}

    public TimesheetRequest(Long employeeId, LocalDate startDate, LocalDate endDate) {
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public TimesheetRequest(Long employeeId, LocalDate startDate, LocalDate endDate, String notes) {
        this(employeeId, startDate, endDate);
        this.notes = notes;
    }

    public TimesheetRequest(
        Long employeeId, 
        LocalDate startDate, 
        LocalDate endDate, 
        String notes, 
        Boolean isResubmission, 
        Long previousTimesheetId
    ) {
        this(employeeId, startDate, endDate, notes);
        this.isResubmission = isResubmission;
        this.previousTimesheetId = previousTimesheetId;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getIsResubmission() {
        return isResubmission;
    }

    public void setIsResubmission(Boolean isResubmission) {
        this.isResubmission = isResubmission;
    }

    public Long getPreviousTimesheetId() {
        return previousTimesheetId;
    }

    public void setPreviousTimesheetId(Long previousTimesheetId) {
        this.previousTimesheetId = previousTimesheetId;
    }

    // === Helper Methods ===

    /**
     * Validate that the request has all required fields
     */
    public boolean isValid() {
        return employeeId != null && employeeId > 0
                && startDate != null
                && endDate != null
                && !endDate .isBefore(startDate);
    }

    /**
     * Get the number of days in this pay period (inclusive)
     */
    public long getPeriodDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    /**
     * Check if this is a weekly timesheet
     */
    public boolean isWeeklyTimesheet() {
        return getPeriodDays() == 7;
    }

    /**
     * Check if this is a bi-weekly timesheet (14-days) 
     */ 
    public boolean isBiWeeklyTimesheet() {
        return getPeriodDays() == 14;
    }

    /**
     * Get user-friendly period description
     */
    public String getPeriodDescription() {
        if (startDate == null || endDate == null) {
            return "Invalid period";
        }
        return String.format("Week of %s to %s (%d days)", startDate.toString(), endDate.toString(), getPeriodDays());
    }

    // === toString() for debugging ===

    @Override
    public String toString() {
        return "TimesheetRequest{" +
                "employeeId=" + employeeId +
                ", startDate=" + startDate + '\'' +
                ", endDate=" + endDate + '\'' +
                ", notes=" + notes + '\'' +
                ", isResubmission=" + isResubmission + '\'' +
                ", previousTimesheetId=" + previousTimesheetId +
                '}';
    }
}
