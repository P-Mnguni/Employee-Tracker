package employee.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "timesheets")
public class Timesheet {
    
    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationship to Employee (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // Pay Period Fields
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // Relationship to TimeEntry (One-to-Many)
    @OneToMany(mappedBy = "timesheet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TimeEntry> timeEntries = new ArrayList<>();

    // Status Field
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimesheetStatus status;                             // PENDING, APPROVED, REJECTED

    // Submission Tracking
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    // Approval Tracking (Future-ready)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;                                // Manager who approved/rejected
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "rejection_reason")
    private String rejectionReason;

    // Timestamps (Future-proofing)
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Timesheet() {}

    public Timesheet(Employee employee, LocalDate startDate, LocalDate endDate) {
        this.employee = employee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = TimesheetStatus.DRAFT;                    // Starts as Draft until submitted
    }

    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status = null) {
            status = TimesheetStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper Methods

    /**
     * Submit the timesheet for manager approval
     */
    public void submit() {
        if (status == TimesheetStatus.DRAFT || status == TimesheetStatus.REJECTED) {
            this.status = TimesheetStatus.PENDING;
            this.submittedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot submit timesheet with status: " + status);
        }
    }

    /**
     * Approve the timesheet
     * @param manager The manager who approved it
     */
    public void approve(Employee manager) {
        if (status == TimesheetStatus.PENDING) {
            this.status = TimesheetStatus.APPROVED;
            this.approvedBy = manager;
            this.approvedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Only PENDING timesheets can be approved. Current status: " + status);
        }
    }

    /**
     * Reject the timesheet
     * @param manager The manager who rejected it
     * @param reason Why it was rejected
     */
    public void reject(Employee manager, String reason) {
        if (status == TimesheetStatus.PENDING) {
            this.status = TimesheetStatus.REJECTED;
            this.approvedBy = manager;
            this.approvedAt = LocalDateTime.now();
            this.rejectionReason = reason;
        } else {
            throw new IllegalStateException("Only PENDING timesheets can be rejected. Current status: " + status);
        }
    }

    /**
     * Add a time entry to this timesheet
     */
    public void addTimeEntry(TimeEntry timeEntry) {
        timeEntries.add(timeEntry);
        timeEntry.setTimesheet(this);
    }

    /**
     * Remove a time entry from this timesheet
     */
    public void removeTimeEntry(TimeEntry timeEntry) {
        timeEntries.remove(timeEntry);
        timeEntry.setTimesheet(null);
    }

    /**
     * Check if timesheet is within a valid date range
     */
    public boolean isValidDateRange() {
        return startDate != null && endDate != null && !endDate.isBefore(startDate);
    }

    /**
     * Check if timesheet is editable (not submitted or rejected)
     */
    public boolean isEditable() {
        return status == TimesheetStatus.DRAFT || status == TimesheetStatus.REJECTED;
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
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<TimeEntry> getTimeEntries() {
        return timeEntries;
    }

    public void setTimeEntries(List<TimeEntry> timeEntries) {
        this.timeEntries = timeEntries;
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
        this.submittedAt = submittedAt;
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
        return "Timesheet{" + 
                "id=" + id + 
                ", employee=" + (employee != null ? employee.getName() : "null") +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                ", timeEntriesCount=" + timeEntries.size() +
                ", submittedAt=" + submittedAt +
                '}';
    }
}
