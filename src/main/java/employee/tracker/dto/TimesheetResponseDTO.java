package employee.tracker.dto;

import employee.tracker.model.TimesheetStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TimesheetResponseDTO - Data transfer object for Timesheet API responses
 * 
 * Contains time entries as IDs only to avoid circular references
 */
public class TimesheetResponseDTO {
    
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeDepartment;
    private LocalDate startDate;
    private LocalDate endDate;
    private TimesheetStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime approvedAt;
    private String approvedBy;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Statistics
    private Integer totalTimeEntries;
    private Double totalHoursWorked;
    private Double totalBreakMinutes;

    // Reference (IDs only, not full object)
    private List<Long> timeEntryIds;
    private List<TimeEntryResponseDTO> timeEntries;

    public TimesheetResponseDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeDepartment() {
        return employeeDepartment;
    }

    public void setEmployeeDepartment(String employeeDepartment) {
        this.employeeDepartment = employeeDepartment;
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

    public Integer getTotalTimeEntries() {
        return totalTimeEntries;
    }

    public void setTotalTimeEntries(Integer totalTimeEntries) {
        this.totalTimeEntries = totalTimeEntries;
    }

    public Double getTotalHoursWorked() {
        return totalHoursWorked;
    }

    public void setTotalHoursWorked(Double totalHoursWorked) {
        this.totalHoursWorked = totalHoursWorked;
    }

    public Double getTotalBreakMinutes() {
        return totalBreakMinutes;
    }

    public void setTotalBreakMinutes(Double totalBreakMinutes) {
        this.totalBreakMinutes = totalBreakMinutes;
    }

    public List<Long> getTimeEntryIds() {
        return timeEntryIds;
    }

    public void setTimeEntryIds(List<Long> timeEntryIds) {
        this.timeEntryIds = timeEntryIds;
    }

    public List<TimeEntryResponseDTO> getTimeEntries() {
        return timeEntries;
    }

    public void setTimeEntries(List<TimeEntryResponseDTO> timeEntries) {
        this.timeEntries = timeEntries;
    }
}
