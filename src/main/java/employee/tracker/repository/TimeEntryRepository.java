package employee.tracker.repository;

import employee.tracker.model.Employee;
import employee.tracker.model.TimeEntry;
import employee.tracker.model.TimeEntryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TimeEntryRepository - Data access layer for TimeEntry entities
 * 
 * Handles all database operations for clock-in/clock-out records
 * Spring Data JPA generates the implementation at runtime
 */
@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    
    // Basic Queries by Employee

    /**
     * Find all time entries for a specific employee
     * Used for: Viewing employee history, timesheet generation
     * 
     * @param employee The employee entity
     * @return List of time entries for the employee
     */
    List<TimeEntry> findByEmployee(Employee employee);

    /**
     * Find all time entries for a specific employee ID
     * 
     * @param employeeId The employee ID
     * @return List of time entries for the employee
     */
    List<TimeEntry> findByEmployeeId(Long employeeId);

    // Queries by Date Range

    /**
     * Find time entries for an employee between specific dates
     * Used for: Weekly/monthly reports, payroll calculations
     * 
     * @param employee The employee entity
     * @param startDate Start of date range (inclusive)
     * @param endDate End of date range (inclusive)
     * @return List of time entries within the date range
     */
    List<TimeEntry> findByEmployeeAndClockInTimeBetween(
        Employee employee,
        LocalDateTime startDate,
        LocalDateTime endDate
    );


    /**
     * Find all time entries between dates (across all employees)
     * Used for: Company-wide reports
     * 
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of time entries within the date range
     */
    List<TimeEntry> findByClockInTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Open Entries (Active Clock-in Sessions)

    /**
     * Find all open time entries for an employee (clocked in but not out)
     * Used for: Preventing double clock-in, detecting active sessions
     * 
     * @param employee The employee entity
     * @return List of open time entries (should be 0 or 1 for normal operation
     */
    List<TimeEntry> findByEmployeeAndClockOutTimeIsNull(Employee employee);

    /**
     * Find all open time entries across all employees
     * Used for: Admin monitoring, active session reports
     * 
     * @return List of all open time entries
     */
    List<TimeEntry> findByClockOutTimeIsNull();

    // Queries by Status

    /**
     * Find time entries by status (PENDING, APPROVED, REJECTED)
     * Used for: Manager approval workflows
     * 
     * @param status The time entry status
     * @return List of time entries with that status
     */
    List<TimeEntry> findByStatus(TimeEntryStatus status);

    /**
     * Find pending time entries fir specific employee
     * Used for: Employee view of unapproved entries
     * 
     * @param employee The employee entity
     * @param status The time entry status
     * @return List of time entries with that status for the employee
     */
    List<TimeEntry> findByEmployeeAndStatus(Employee employee, TimeEntryStatus status);

    /**
     * Find pending entries by employee ID
     * 
     * @param employeeId The employee ID
     * @param status The time entry status
     * @return List of pending time entries
     */
    List<TimeEntry> findByEmployeeIdAndStatus(Long employeeId, TimeEntryStatus status);

    // Advanced Queries with JPQL

    /**
     * Find time entries for an employee with a specific date range
     * (Alternative using @Query for more complex logic)
     * 
     * @param employeeId The employee ID
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of time entries
     */
    @Query("SELECT t FROM TimeEntry t WHERE t.employee.id = :employeeId " +
        "AND DATE(t.clockInTime) BETWEEN :startDate AND :endDate")
    List<TimeEntry> findEntriesByEmployeeAndDateRange(
        @Param("employeeId") Long employeeId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Count total hours worked for an employee in a date range
     * Used for: Payroll calculations, overtime detection
     * 
     * @param employeeId The employee ID
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return Total minutes worked (to be converted to hours)
     */
    @Query("SELECT SUM(FUNCTION('TIMESTAMPDIFF', MINUTE, t.clockInTime, t.clockOutTime)) " +
        "FROM TimeEntry t WHERE t.employee.id = :employeeId " +
        "AND t.status = 'APPROVED' " +
        "AND t.clockInTime BETWEEN :startDate AND :endDate")
    Long getTotalMinutesWorked(
        @Param("employeeId") Long employeeId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    // Batch Operations

    /**
     * Find all time entries for multiple employees in a date range
     * Used for: Department/team reporting
     * 
     * @param employeeIds List of employee IDs
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of time entries
     */
    List<TimeEntry> findByEmployeeIdInAndClockInTimeBetween(
        List<Long> employeeIds,
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    // Aggregation Queries

    /**
     * Count total time entries for an employee
     * Used for: Statistics, activity tracking
     * 
     * @param employeeId The employee ID
     * @return Count of time entries
     */
    long countByEmployeeId(Long employeeId);

    /**
     * Check if an employee has any open time entry
     * Used for: Clock-in validation (prevent double clock-in)
     * 
     * @param employeeId The employeeId
     * @return True if employee has open entry
     */
    boolean existsByEmployeeIdAndClockOutTimeIsNull(Long employeeId);
}
