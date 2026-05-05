package employee.tracker.dto;

import employee.tracker.model.LeaveType;
import employee.tracker.model.PTOStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * PTOResponse DTO - Represents what the API returns for a PTO/leave request
 * 
 * Flat, simple structure with no nested objects or entity references.
 * Prevents circular references and keeps API responses clean and lightweight.
 * 
 * Example response:
 * {
 *      "id": 1,
 *      "employeeId": 1,
 *      "startDate": "2026-05-01",
 *      "endDate": "2026-05-03",
 *      "type": "PTO",
 *      "status": "APPROVED",
 *      "reason": "Personal leave",
 *      "requestedAt": "2026-04-29T10:00:00"
 * }
 */
public class PTOResponse {
    
    /**
     * Unique identifier of the PTO request
     */
    private Long id;
    
    /**
     * Start date of the leave (inclusive)
     */
    private LocalDate startDate;
    
    /**
     * End date of the leave (inclusive)
     */
    private LocalDate endDate;

    /**
     * Current status (PENDING, APPROVED, REJECTED, CANCELLED)
     */
    private PTOStatus status;

    /**
     * Type of leave (PTO, SICK, UNPAID)
     */
    private LeaveType type;

    /**
     * Reason for the leave request (what the employee provided)
     */
    private String reason;

    /**
     * When the request was submitted
     */
    private LocalDateTime requestedAt;

    /**
     * ID of the employee who made this request
     * Using ID only prevents circular references
     */
    private Long employeeId;

    /**
     * Number of days requested
     */
    private Double daysRequested;

    /**
     * Whether this is a partial day request (e.g., half day)
     */
    private Boolean isPartialDay;

    /**
     * Additional notes for the manager
     */
    private String notes;
    
    /**
     * When the request was approved/rejected (null if still pending)
     */
    private LocalDateTime approvedAt;

    /**
     * Name of the manager who approved/rejected
     */
    private String approvedBy;

    /**
     * Reason for rejection (if needed)
     */
    private String rejectionReason;

    public PTOResponse() {}

    public PTOResponse(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        PTOStatus status,
        LeaveType type,
        String reason,
        LocalDateTime requestedAt,
        Long employeeId
    ) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.type = type;
        this.reason = reason;
        this.requestedAt = requestedAt;
        this.employeeId = employeeId;
    }

    public PTOResponse(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        PTOStatus status,
        LeaveType type,
        String reason,
        LocalDateTime requestedAt,
        Long employeeId,
        Double daysRequested,
        Boolean isPartialDay
    ) {
        this(id, startDate, endDate, status, type, reason, requestedAt, employeeId);
        this.daysRequested = daysRequested;
        this.isPartialDay = isPartialDay;
    }

    public PTOResponse(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        PTOStatus status,
        LeaveType type,
        String reason,
        LocalDateTime requestedAt,
        Long employeeId,
        Double daysRequested,
        Boolean isPartialDay,
        String notes,
        LocalDateTime approvedAt,
        String approvedBy,
        String rejectionReason
    ) {
        this(id, startDate, endDate, status, type, rejectionReason, requestedAt, employeeId, daysRequested, isPartialDay);
        this.notes = notes;
        this.approvedAt = approvedAt;
        this.approvedBy = approvedBy;
        this.rejectionReason = rejectionReason;
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

    public PTOStatus getStatus() {
        return status;
    }

    public void setStatus(PTOStatus status) {
        this.status = status;
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

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Double getDaysRequested() {
        return daysRequested;
    }

    public void setDaySRequested(Double daysRequested) {
        this.daysRequested = daysRequested;
    }

    public Boolean getIsPartialDay() {
        return isPartialDay;
    }

    public void setIsPartialDay(Boolean isPartialDay) {
        this.isPartialDay = isPartialDay;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    // Helper methods

    /**
     * Check if the request is approved
     */
    public boolean isApproved() {
        return status == PTOStatus.APPROVED;
    }

    /**
     * Check if the request is pending
     */
    public boolean isPending() {
        return status == PTOStatus.PENDING;
    }

    /**
     * Check if the request is rejected
     */
    public boolean isRejected() {
        return status == PTOStatus.REJECTED;
    }

    /**
     * Get number of days between start and end (inclusive)
     */
    public long getCalculatedDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    // toString() for debugging

    @Override
    public String toString() {
        return "PTOResponse{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", startDate=" + startDate + 
                ", endDate=" + endDate +
                ", type=" + type +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                ", daysRequested=" + daysRequested +
                ", requestedAt=" + requestedAt +
                '}';
    }
}
