package employee.tracker.repository;

import employee.tracker.model.Employee;
import employee.tracker.model.Timesheet;
import employee.tracker.model.TimesheetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TimesheetRepository - Data access layer for Timesheet entities
 * 
 * Handles all database operations for timesheets, including approval workflows,
 * payroll grouping, and reporting
 */
@Repository
public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {
    
    // Basic Queries by Employee

    /**
     * Find all timesheets for a specific employee
     * Used for: Employee viewing their timesheet history
     * 
     * @param employee The employee entity
     * @return List of timesheets for the employee
     */
    List<Timesheet> findByEmployee(Employee employee);

    /**
     * Find all timesheets for a specific employee ID
     * 
     * @param employeeId The employee ID
     * @return List of timesheets for the employee
     */
    List<Timesheet> findByEmployeeId(Long employeeId);

    // Queries by Status

    /**
     * Find timesheets by status (DRAFT, PENDING, APPROVED, REJECTED)
     * Used for: Manager viewing pending approvals, filtering by status
     * 
     * @param status The timesheet status
     * @return List of timesheets with that status
     */
    List<Timesheet> findByStatus(TimesheetStatus status);

    /**
     * Find timesheets for a specific employee by status
     * Used for: Employee viewing their pending/approved timesheets
     * 
     * @param employee The employee entity
     * @param status The timesheet status
     * @return List of timesheets matching both criteria
     */
    List<Timesheet> findByEmployeeAndStatus(Employee employee, TimesheetStatus status);

    /**
     * Find pending timesheets for a specific employee ID
     * 
     * @param employeeId The employee ID
     * @param status The timesheet status
     * @return List of pending timesheets for the employee
     */
    List<Timesheet> findByEmployeeIdAndStatus(Long employeeId, TimesheetStatus status);

    // Queries by Date Range

    /**
     * Find timesheets for an employee where start date is between given dates
     * Used for: Payroll periods, historical records
     * 
     * @param employee The employee entity
     * @param startDate Start of date range (inclusive)
     * @param endDate End of date range (inclusive)
     * @return List of timesheets within the date range
     */
    List<Timesheet> findByEmployeeAndStartDateBetween(
        Employee employee,
        LocalDate startDate,
        LocalDate endDate
    );

    /**
     * Find timesheets where start date is between given dates (all employees)
     * Used for: Company-wide payroll processing
     * 
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of timesheets within the date range
     */
    List<Timesheet> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find timesheets for a specific date range by employee ID
     * 
     * @param employeeId The employee ID
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of timesheets within the date range
     */
    List<Timesheet> findByEmployeeIdAndStartDateBetween(
        Long employeeId,
        LocalDate startDate,
        LocalDate endDate
    );

    // Manager Dashboard Queries 

    /**
     * Find pending timesheets for employees in a specific department
     * Used for: Department managers viewing their team's pending timesheets
     * 
     * @param department The department name
     * @param status The timesheet status (typically PENDING)
     * @return List of pending timesheets for the department
     */
    @Query("SELECT t FROM Timesheet t WHERE t.employee.department = :department " +
        "AND t.status = :status")
    List<Timesheet> findByEmployeeDepartmentAndStatus(
        @Param("department") String department,
        @Param("status") TimesheetStatus status
    );
    
    /**
     * Count pending timesheets for a manager's team
     * Used for: Dashboard notification badges
     * 
     * @param department The department name
     * @param status The timesheet status (typically PENDING)
     * @return Count of pending timesheets
     */
    long countByEmployeeDepartmentAndStatus(String department, TimesheetStatus status);

    // Payroll Processing 

    /**
     * Find approved timesheets for a date range
     * Used for: Payroll processing (only approved timesheets should be paid)
     * 
     * @param status The timesheet status (APPROVED)
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of approved timesheets
     */
    List<Timesheet> findByStatusAndStartDateBetween(
        TimesheetStatus status,
        LocalDate startDate,
        LocalDate endDate
    );

    /**
     * Find approved timesheets for a specific employee in a date range
     * Used for: Employee pay stubs
     * 
     * @param employeeId The employee ID
     * @param status The timesheet status (APPROVED)
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of approved timesheets
     */
    List<Timesheet> findByEmployeeIdAndStatusAndStartDateBetween(
        Long employeeId,
        TimesheetStatus status,
        LocalDate startDate,
        LocalDate endDate
    );

    // Timesheet Period Queries

    /**
     * Check if a timesheet already exists for an employee in a given period
     * Used for: Preventing duplicate timesheet creation
     * 
     * @param employeeId The employee ID
     * @param startDate Start of pay period
     * @param endDate End of pay period
     * @return True if timesheet exists
     */
    boolean existsByEmployeeIdAndStartDateAndEndDate(
        Long employeeId,
        LocalDate startDate,
        LocalDate endDate
    );

    /**
     * Find timesheet for an employee in a specific pay period
     * Used for: Editing existing timesheets
     * 
     * @param employeeId The employee ID
     * @param startDate Start of pay period
     * @param endDate End of pay period
     * @return Optional containing the timesheet if found
     */
    @Query("SELECT t FROM Timesheet t WHERE t.employee.id = :employeeId " +
        "AND t.startDate = :startDate AND t.endDate = :endDate")
    List<Timesheet> findByEmployeeIdAndPayPeriod(
        @Param("employeeId") Long employeeId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // Reporting Queries

    /**
     * Find all timesheets submitted after a specific date
     * Used for: Recent activity reports
     * 
     * @param submittedAfter Date threshold
     * @return List of timesheets submitted after the date
     */
    List<Timesheet> findBySubmittedAtAfter(LocalDateTime submittedAfter);

    /**
     * Find timesheets approved by a specific manager
     * Used for: Manager audit trails
     * 
     * @param managerId The manager's employee ID
     * @return List of timesheets approved by this manager
     */
    List<Timesheet> findByApprovedById(Long managerId);

    /**
     * Get all timesheets for a department in a date range
     * Used for: Department-wide reports
     * 
     * @param department The department name
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of timesheets
     */
    @Query("SELECT t FROM Timesheet t WHERE t.employee.department = :department " +
        "AND t.startDate BETWEEN :startDate AND :endDate")
    List<Timesheet> findByDepartmentAndDateRange(
        @Param("department") String department,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
