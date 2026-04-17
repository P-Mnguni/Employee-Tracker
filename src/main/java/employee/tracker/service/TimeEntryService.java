package employee.tracker.service;

import employee.tracker.model.Employee;
import employee.tracker.model.TimeEntry;
import employee.tracker.model.TimeEntryStatus;
import employee.tracker.repository.EmployeeRepository;
import employee.tracker.repository.TimeEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TimeEntryService - Core time tracking business logic
 * 
 * This service handles all clock-in/clock-out operations and enforces
 * business rules such as preventing double clock-in and ensuring proper
 * clock-out sequences.
 */
@Service
@Transactional
public class TimeEntryService {
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TimeEntryRepository timeEntryRepository;

    /**
     * Clock in an employee
     * 
     * Business Rules:
     * 1. Employee must exist
     * 2. Employee cannot already be clocked in (no open entry)
     * 3. Entry starts with PENDING status
     * 4. Clock-in time is set to current timestamp
     * 
     * @param employeeId The ID of the employee clocking in
     * @return The newly created TimeEntry
     * @throws RuntimeException if employee not found or already clocked in
     */
    public TimeEntry clockIn(Long employeeId) {
        // Step 1: Find the employee
        Employee employee = employeeRepository.findById(employeeId)
                            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        // Step 2: Check if already clocked in (open entry exists)
        List<TimeEntry> openEntries = timeEntryRepository.findByEmployeeAndClockOutTimeIsNull(employee);

        if (!openEntries.isEmpty()) {
            throw new IllegalStateException("Employee is already clocked in! Please clock out before clocking in again.");
        }

        // Step 3: Create new time entry
        TimeEntry timeEntry = new TimeEntry(employee, LocalDateTime.now());

        // Step 4: Set status (already PENDING by default, but explicit for clarity)
        timeEntry.setStatus(TimeEntryStatus.PENDING);

        // Step 5: Save and return
        return timeEntryRepository.save(timeEntry);
    }

    /**
     * Clock out an employee
     * 
     * Business RUles:
     * 1. Employee must have an open entry (clocked in without clock-out)
     * 2. Clock-out time must be after clock-in time (validated by entity)
     * 3. Entry remains PENDING until manager approval
     * 
     * @param employeeId The ID of the employee clocking out
     * @return The updated TimeEntry with clock-out time set
     * @throws RuntimeException if no open entry found
     */
    public TimeEntry clockOut(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        List<TimeEntry> openEntries = timeEntryRepository.findByEmployeeAndClockOutTimeIsNull(employee);

        if (openEntries.isEmpty()) {
            throw new IllegalStateException("Employee is not clocked in! Cannot clock out without first clocking in.");
        }

        TimeEntry activeEntry = openEntries.get(0);
        activeEntry.clockOut();                             // Sets to current time

        return timeEntryRepository.save(activeEntry);
    }

    /**
     * Clock out an employee with a specific time (for corrections/back-dating)
     * 
     * Business Rules:
     * 1. Employee must have an open entry
     * 2. Clock-out time must be after clock-in time
     * 3. Useful for admin corrections or missed clock-outs
     * 
     * @param employeeId The ID of the employee clocking out
     * @param clockOutTime The specific time to set as clock-out
     * @return The updated TimeEntry
     * @throws RuntimeException if no open entry found or time is invalid
     */
    public TimeEntry clockOutWithTime(Long employeeId, LocalDateTime clockOutTime) {
        Employee employee = employeeRepository.findById(employeeId)
                            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        List<TimeEntry> openEntries = timeEntryRepository.findByEmployeeAndClockOutTimeIsNull(employee);

        if (openEntries.isEmpty()) {
            throw new IllegalStateException("Employee is not clocked in! Cannot clock out without first clocking in.");
        }

        TimeEntry activeEntry = openEntries.get(0);
        activeEntry.clockOut(clockOutTime);

        return timeEntryRepository.save(activeEntry);
    }

    /**
     * Get all time entries for an employee
     * 
     * @param employeeId The ID of the employee
     * @return List of all time entries for the employee
     * @throws RuntimeException if employee not found
     */
    public List<TimeEntry> getEmployeeEntries(Long employeeId) {
        // Verify employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new RuntimeException("Employee not found with id: " + employeeId);
        }

        return timeEntryRepository.findByEmployeeId(employeeId);
    }

    /**
     * Get time entries for an employee within a date range
     * 
     * @param employeeId The ID of the employee
     * @param startDate Start of date range
     * @return List of time entries within the date range
     */
    public List<TimeEntry> getEmployeeEntriesByDateRange(Long employeeId, LocalDateTime startDate, LocalDateTime endDate) {
        Employee employee = employeeRepository.findById(employeeId)
                            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        return timeEntryRepository.findByEmployeeAndClockInTimeBetween(employee, startDate, endDate);
    }

    /**
     * Get the current active session for an employee (if any)
     * 
     * @param employeeId The ID of the employee
     * @return The active TimeEntry if exists, null otherwise
     */
    public TimeEntry getActiveSession(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        List<TimeEntry> openEntries = timeEntryRepository.findByEmployeeAndClockOutTimeIsNull(employee);

        return openEntries.isEmpty() ? null : openEntries.get(0);
    }

    /**
     * Check if an employee is currently clocked in
     * 
     * @param employeeId The ID of the employee
     * @return true if employee has an open entry, false otherwise
     */
    public boolean isClockedIn(Long employeeId) {
        return timeEntryRepository.existsByEmployeeIdAndClockOutTimeIsNull(employeeId);
    }

    /**
     * Get all pending time entries (for manager approval)
     * 
     * @return List of all pending time entries across all employees
     */
    public List<TimeEntry> getAllPendingEntries() {
        return timeEntryRepository.findByStatus(TimeEntryStatus.PENDING);
    }

    /**
     * Get pending time entries for a specific employee
     * 
     * @param employeeId The ID of the employee
     * @return List of pending entries for the employee
     */
    public List<TimeEntry> getEmployeePendingEntries(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        return timeEntryRepository.findByEmployeeAndStatus(employee, TimeEntryStatus.PENDING);
    }

    /**
     * Get today's entries for an employee
     * 
     * @param employeeId The ID of the employee
     * @return List of today's time entries
     */
    public List<TimeEntry> getTodayEntries(Long employeeId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);

        return getEmployeeEntriesByDateRange(employeeId, startOfDay, endOfDay);
    }
}
