package employee.tracker.dto;

import employee.tracker.model.TimesheetStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TimesheetResponse DTO - Represents what the API returns for a timesheet.
 * 
 * Flat structure using TimeEntryResponse DTOs to prevent circular references.
 * This breaks the recursion: Timesheet → TimeEntry → Employee → Timesheet
 * 
 * Example response:
 * {
 *      "id": 1,
 *      "startDate": "2026-04-01",
 *      "endDate": "2026-04-07",
 *      "status": "APPROVED",
 *      "employeeId": 1,
 *      "submittedAt": "2026-04-07T17:00:00",
 *      "entries": [
 *      {
 *          "id": 10,
 *          "clockInTime": "2026-04-01T08:00:00",
 *          "clockOutTime": "2026-04-01T17:00:00",
 *          "status": "APPROVED",
 *          "employeeId": 1
 *      }
 *  ]
 * }
 */
public class TimesheetResponse {
    
    /**
     * Unique identifier of the timesheet
     */
    private Long id;

    /**
     * Start date of the pay period (inclusive)
     */
    private LocalDate startDate;

    /**
     * End date of the pay period (inclusive)
     */
    private LocalDate endDate;

    /**
     * Current status (DRAFT, PENDING, APPROVED, REJECTED)
     */
    private TimesheetStatus status;

    /**
     * When the timesheet was submitted (null if still in DRAFT)
     */
    private LocalDateTime submittedAt;

    /**
     * ID of the employee who owns this timesheet
     * Using ID only prevents circular references
     */
    private Long employeeId;

    /**
     * List of time entries in this timesheet
     * Using TimeEntryResponse DTOs, not the actual TimeEntry entities
     * This prevents circular references back to the timesheet
     */
    private List<TimeEntryResponse> entries;

    /**
     * Total hours worked across all entries
     */
    private Double totalHours;
    
    public TimesheetResponse() {}

    public TimesheetResponse(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        TimesheetStatus status,
        LocalDateTime submittedAt,
        Long employeeId
    ) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.submittedAt = submittedAt;
        this.employeeId = employeeId;
    }

    public TimesheetResponse(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        TimesheetStatus status,
        LocalDateTime submittedAt,
        Long employeeId,
        List<TimeEntryResponse> entries
    ) {
        this(id, startDate, endDate, status, submittedAt, employeeId);
        this.entries = entries;
    }

    public TimesheetResponse(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        TimesheetStatus status,
        LocalDateTime submittedAt,
        Long employeeId,
        List<TimeEntryResponse> entries,
        Double totalHours
    ) {
        this(id, startDate, endDate, status, submittedAt, employeeId, entries);
        this.totalHours = totalHours;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public TimesheetStatus getStatus() {
        return status;
    }

    public void setStatus(TimesheetStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt =submittedAt;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public List<TimeEntryResponse> getEntries() {
        return entries;
    }

    public void setEntries(List<TimeEntryResponse> entries) {
        this.entries = entries;
    }

    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }

    // Helper method

    /**
     * Calculate total hours from all entries
     */
    public void calculateTotalHours() {
        if (entries != null && !entries.isEmpty()) {
            this.totalHours = entries.stream()
                                .filter(entry -> entry.getTotalHours() != null)
                                .mapToDouble(TimeEntryResponse::getTotalHours)
                                .sum();
        } else {
            this.totalHours = 0.0;
        }
    }

    // toString() for debugging

    @Override
    public String toString() {
        return "TimesheetResponse{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", submittedAt=" + submittedAt +
                ", employeeId=" + employeeId +
                ", entriesCount=" + (entries != null ? entries.size() : 0) +
                ", totalHours=" + totalHours +
                '}';
    }
}
