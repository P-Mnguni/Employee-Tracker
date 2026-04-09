package employee.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pto_requests")
public class PTORequest {
    
    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationship to Employee (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // Core Fields - Leave Period
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // Leave Type
    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false)
    private LeaveType leaveType;                             // PTO, SICK, UNPAID, etc.

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PTOStatus status;                            // PENDING, APPROVED, REJECTED, CANCELLED

    // Reason
    @Column(length = 500)
    private String reason;

    // Submission Tracking
    @Column(name = "requested_id", updatable = false)
    private LocalDateTime requestedAt;

    // Approval Tracking (Future-ready)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    // Additional Fields for PTO Tracking
    @Column(name = "days_requested")
    private Double daysRequested;                           // Number of days (e.g., 0.5 for half day)

    @Column(name = "is_partial_by")
    private Boolean isPartialDay = false;                   // Half-day request?

    // Timestamps (Future-proofing)
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public PTORequest() {}

    public PTORequest(Employee employee, LocalDate startDate, LocalDate endDate, LeaveType leaveType, String reason) {
        this.employee = employee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = leaveType;
        this.reason = reason;
        this.status = PTOStatus.PENDING;
        this.requestedAt = LocalDateTime.now();

        // Calculate days requested
        this.daysRequested = calculateDays(startDate, endDate);
        this.isPartialDay = false;
    }

    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (requestedAt == null) {
            requestedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = PTOStatus.PENDING;
        }
        if (isPartialDay == null) {
            isPartialDay = false;
        }
        if (daysRequested == null && startDate != null && endDate != null) {
            daysRequested = calculateDays(startDate, endDate);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper Methods

    /**
     * Simple calculation of days between dates (inclusive)
     * Basic calculation - real logic with business days will be in service
     */
    private double calculateDays(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0.0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
    }

    /**
     * Approve the PTO request
     * @param approver The manager who approved it
     */
    public void approve(Employee approver) {
        this.status = PTOStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
    }

    /**
     * Reject the PTO request
     * @param approver The manager who rejected it
     * @param reason Why it was rejected
     */
    public void reject(Employee approver, String reason) {
        this.status = PTOStatus.REJECTED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
        this.rejectionReason = reason;
    }

    /**
     * Cancel the PTO request (employee withdraws before approval)
     */
    public void cancel() {
        if (status == PTOStatus.PENDING) {
            this.status = PTOStatus.CANCELLED;
        } else {
            throw new IllegalStateException("Cannot cancel request with status: " + status);
        }
    }

    /**
     * Check if request is still pending
     */
    public boolean isPending() {
        return status == PTOStatus.PENDING;
    }

    /**
     * Check if request is approved
     */
    public boolean isApproved() {
        return status == PTOStatus.APPROVED;
    }

    /**
     * Check if date range is valid (start before end)
     */
    public boolean isValidDateRange() {
        return startDate != null && endDate != null && !endDate.isBefore(startDate);
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;

        if (this.endDate != null) {
            this.daysRequested = calculateDays(startDate, this.endDate);
        }
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;

        if (this.startDate != null) {
            this.daysRequested = calculateDays(this.startDate, endDate);
        }
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public PTOStatus getStatus() {
        return status;
    }

    public void setStatus(PTOStatus status) {
        this.status = status;
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

    public Employee getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Employee approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Double getDaysRequested() {
        return daysRequested;
    }

    public void setDaysRequested(Double daysRequested) {
        this.daysRequested = daysRequested;
    }

    public Boolean getIsPartialDay() {
        return isPartialDay;
    }

    public void setIsPartialDay(Boolean isPartialDay) {
        this.isPartialDay = isPartialDay;
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
        return "PTORequest{" +
                "id=" + id +
                ", employee=" + (employee != null ? employee.getName() : "null") +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", leaveType=" + leaveType +
                ", status=" + status +
                ", daysRequested=" + daysRequested +
                ", requestedAt=" + requestedAt +
                '}';
    }
}
