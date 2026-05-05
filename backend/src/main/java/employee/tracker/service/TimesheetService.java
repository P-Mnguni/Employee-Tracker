package employee.tracker.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import employee.tracker.model.Employee;
import employee.tracker.model.TimeEntry;
import employee.tracker.model.TimeEntryStatus;
import employee.tracker.model.Timesheet;
import employee.tracker.model.TimesheetStatus;
import employee.tracker.repository.EmployeeRepository;
import employee.tracker.repository.TimeEntryRepository;
import employee.tracker.repository.TimesheetRepository;
import jakarta.transaction.Transactional;

/**
 * TimesheetService - Timesheet business logic for submission and approval workflows
 * 
 * This service handles the entire timesheet lifecycle including:
 * - Creating/submitting timesheets from time entries
 * - Manager approval/rejection of timesheet
 * - Timesheet retrieval for employees and managers
 */
@Service
@Transactional
public class TimesheetService {

    @Autowired
    private TimesheetRepository timesheetRepository;

    @Autowired
    private TimeEntryRepository timeEntryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Submit a timesheet for a specific pay period
     * 
     * Business RUles:
     * 1. Employee must exists
     * 2. Time entries must exist within the date range
     * 3. Cannot submit duplicate timesheet for same period
     * 4. Timesheet starts with PENDING status
     * 5. All time entries in the timesheet must be in PENDING status
     * 
     * @param employeeId The ID of the employee submitting the timesheet
     * @param startDate Start date of the pay period
     * @param endDate End date of the pay period
     * @return The created Timesheet
     * @throws RuntimeException if validation fails
     */
    public Timesheet submitTimesheet(Long employeeId, LocalDate startDate, LocalDate endDate) {
        Employee employee = employeeRepository.findById(employeeId)
                            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        if (timesheetRepository.existsByEmployeeIdAndStartDateAndEndDate(employeeId, startDate, endDate)) {
            throw new IllegalStateException("Timesheet already exists for period: " + startDate + " to " + endDate);
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<TimeEntry> timeEntries = timeEntryRepository.findByEmployeeAndClockInTimeBetween(employee, startDateTime, endDateTime);

        if (timeEntries.isEmpty()) {
            throw new IllegalStateException("No time entries found for period: " + startDate + 
                                            " to " + endDate + ". Cannot submit empty timesheet.");
        }

        boolean hasNonPending = timeEntries.stream().anyMatch(entry -> entry.getStatus() != TimeEntryStatus.PENDING);

        if (hasNonPending) {
            throw new IllegalStateException("Some time entries have already been processed. " + 
                                            "Cannot submit timesheet with approved/rejected entries.");
        }

        Timesheet timesheet = new Timesheet(employee, startDate, endDate);

        for (TimeEntry entry : timeEntries) {
            timesheet.addTimeEntry(entry);
        }

        timesheet.submit();

        return timesheetRepository.save(timesheet);
    }

    /**
     * Approve a timesheet
     * 
     * Business Rule:
     * 1. Timesheet must exist
     * 2. Timesheet must be in PENDING status
     * 3. Only managers should be allowed (role check in controller layer)
     * 4. All time entries in the timesheet are automatically approved
     * 
     * @param timesheetId The ID of the timesheet to approve
     * @param managerId The ID of the manager approving the timesheet
     * @return The approved Timesheet
     * @throws RuntimeException if validation fails
     */
    public Timesheet approveTimesheet(Long timesheetId, Long managerId) {
        Timesheet timesheet = timesheetRepository.findById(timesheetId)
                                .orElseThrow(() -> new RuntimeException("Timesheet not found with id: " + timesheetId));

        if (timesheet.getStatus() != TimesheetStatus.PENDING) {
            throw new IllegalStateException("Cannot approve timesheet with status: " + timesheet.getStatus() + 
                                            ". Only PENDING timesheets can be approved.");
        }

        Employee manager = employeeRepository.findById(managerId)
                            .orElseThrow(() -> new RuntimeException("Manager not found with id: " + managerId));
        timesheet.approve(manager);

        for (TimeEntry entry : timesheet.getTimeEntries()) {
            if (entry.getStatus() == TimeEntryStatus.PENDING) {
                entry.approve();
            }
        }

        timeEntryRepository.saveAll(timesheet.getTimeEntries());

        return timesheetRepository.save(timesheet);
    }

    /**
     * Reject a timesheet
     * 
     * Business Rules:
     * 1. Timesheet must exist
     * 2. Timesheet must be in PENDING status
     * 3. Only managers should be allowed (role check in controller layer)
     * 4. Time entries remain PENDING so employee can edit and resubmit
     * 
     * @param timesheetId The ID of the timesheet to reject
     * @param managerId The ID of the manager rejecting the timesheet
     * @param reason The reason for rejection (optional but recommended)
     * @return The rejected Timesheet
     * @throws RuntimeException if validation fails
     */
    public Timesheet rejectTimesheet(Long timesheetId, Long managerId, String reason) {
        Timesheet timesheet = timesheetRepository.findById(timesheetId)
                                .orElseThrow(() -> new RuntimeException("Timesheet not found with id: " + timesheetId));

        if (timesheet.getStatus() != TimesheetStatus.PENDING) {
            throw new IllegalStateException("Cannot reject timesheet with status: " + timesheet.getStatus() +
                                            ". Only PENDING timesheets can be rejected");
        }

        Employee manager = employeeRepository.findById(managerId)
                            .orElseThrow(() -> new RuntimeException("Manager not found with id: " + managerId));
        timesheet.reject(manager, reason);

        // Time entries remain PENDING so employees can fix and resubmit
        // Timesheet will go back to DRAFT and can be edited/resubmitted
        return timesheetRepository.save(timesheet);
    }

    /**
     * Get all timesheets for an employee
     * 
     * @param employeeId The ID of the employee
     * @return List of all timesheets for the employee
     * @throws RuntimeException if employee not found
     */
    public List<Timesheet> getEmployeeTimesheets(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new RuntimeException("Employee not found with id: " + employeeId);
        }
        return timesheetRepository.findByEmployeeId(employeeId);
    }

    /**
     * Get timesheets for an employee within a date range
     * 
     * @param employeeId The ID of the employee
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of timesheets within the date range
     */
    public List<Timesheet> getEmployeeTimesheetsByDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        Employee employee = employeeRepository.findById(employeeId)
                            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        return timesheetRepository.findByEmployeeAndStartDateBetween(employee, startDate, endDate);
    }

    /**
     * Get all pending timesheets (for manager approval dashboard)
     * 
     * @return List of all pending timesheets across all employees
     */
    public List<Timesheet> getAllPendingTimesheets() {
        return timesheetRepository.findByStatus(TimesheetStatus.PENDING);
    }

    /**
     * Get pending timesheets for a specific department (for department managers)
     * 
     * @param department The department name
     * @return List of pending timesheets for employees in the department
     */
    public List<Timesheet> getPendingTimesheetsByDepartment(String department) {
        return timesheetRepository.findByEmployeeDepartmentAndStatus(department, TimesheetStatus.PENDING);
    }

    /**
     * Get approved timesheets for payroll processing
     * 
     * @param startDate Start of payroll period
     * @param endDate End of payroll period
     * @return List of approved timesheets in the date range
     */
    public List<Timesheet> getApprovedTimesheetsForPayroll(LocalDate startDate, LocalDate endDate) {
        return timesheetRepository.findByStatusAndStartDateBetween(TimesheetStatus.APPROVED, startDate, endDate);
    }

    /**
     * Get timesheet by ID with full detail
     * 
     * @param timesheetId The ID of the timesheet
     * @return The Timesheet entity
     */
    public Timesheet getTimesheetById(Long timesheetId) {
        return timesheetRepository.findById(timesheetId)
                                .orElseThrow(() -> new RuntimeException("Timesheet not found with id: " + timesheetId));
    }

    /**
     * Check if a timesheet exists for a specific period
     * 
     * @param employeeId The ID of the employee
     * @param startDate Start of pay period
     * @param endDate End of pay period
     * @return true if timesheet exists, false otherwise
     */
    public boolean timesheetExistsForPeriod(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return timesheetRepository.existsByEmployeeIdAndStartDateAndEndDate(employeeId, startDate, endDate);
    }

    /**
     * Get timesheet statistics for an employee
     * 
     * @param employeeId The ID of the employee
     * @return Array of counts: [total, pending, approved, rejected]
     */
    public long[] getTimesheetStatistics(Long employeeId) {
        List<Timesheet> timesheets = getEmployeeTimesheets(employeeId);

        long total = timesheets.size();
        long pending = timesheets.stream().filter(t -> t.getStatus() == TimesheetStatus.PENDING).count();
        long approved = timesheets.stream().filter(t -> t.getStatus() == TimesheetStatus.APPROVED).count();
        long rejected = timesheets.stream().filter(t -> t.getStatus() == TimesheetStatus.REJECTED).count();

        return new long[]{total, pending, approved, rejected};
    }
}
