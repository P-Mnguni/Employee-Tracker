package employee.tracker.repository;

import employee.tracker.model.Employee;
import employee.tracker.model.PTORequest;
import employee.tracker.model.PTOStatus;
import employee.tracker.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * PTORequestRepository - Data access layer for PTORequest entities
 * Handles all database operations for leave requests, including approval workflows
 * employee tracking, and HR reporting.
 */
@Repository
public interface PTORequestRepository extends JpaRepository<PTORequest, Long> {
    
    // Basic Queries by Employee

    /**
     * Find all PTO requests for a specific employee
     * Used for: Employee viewing their leave history
     * 
     * @param employee The employee entity
     * @return List of PTO requests for the employee
     */
    List<PTORequest> findByEmployee(Employee employee);

    /**
     * Find all PTO requests for a specific employee ID
     * 
     * @param employeeId The employee ID
     * @return List of PTO requests for the employee
     */
    List<PTORequest> findByEmployeeId(Long employeeId);

    // Queries by Status

    /**
     * Find PTO requests by status (PENDING, APPROVED, REJECTED)
     * Used for: Managers reviewing pending requests
     * 
     * @param status The PTO status
     * @return List of PTO requests with that status
     */
    List<PTORequest> findByStatus(PTOStatus status);

    /**
     * Find PTO requests for a specific employee by status
     * Used for: Employee viewing their pending/approved requests
     * 
     * @param employee The employee entity
     * @param status The PTO status
     * @return List of PTO requests matching both criteria
     */
    List<PTORequest> findByEmployeeAndStatus(Employee employee, PTOStatus status);

    /**
     * Find pending requests for a specific employee ID
     * 
     * @param employeeId The employee ID
     * @param status The PTO status
     * @return List of pending PTO requests for the employee
     */
    List<PTORequest> findByEmployeeIdAndStatus(Long employeeId, PTOStatus status);

    // Queries by Date Range

    /**
     * Find PTO requests where start date is between given dates
     * Used for: Reporting, tracking leave trends
     * 
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of PTO requests within the date range
     */
    List<PTORequest> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find PTO requests for an employee where date range overlaps with given dates
     * Used for: Checking for scheduling conflicts
     * 
     * @param employee The employee entity
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of overlapping PTO requests
     */
    @Query("SELECT p FROM PTORequest p WHERE p.employee = :employee " +
        "AND p.status = 'APPROVED' " +
        "AND p.startDate <= :endDate AND p.endDate >= :startDate")
    List<PTORequest> findOverlappingRequests(
        @Param("employee") Employee employee,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find PTO requests for an employee in a specific date range
     * Used for: Employee leave calendar
     * 
     * @param employeeId The employee ID
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of PTO requests within the date range
     */
    List<PTORequest> findByEmployeeIdAndStartDateBetween(
        Long employeeId,
        LocalDate startDate,
        LocalDate endDate
    );

    // Manager Dashboard Queries

    /**
     * Find pending PTO requests for employees in a specific department
     * Used for: Department managers viewing their team's pending requests
     * 
     * @param department The department name
     * @param status The PTO status (typically PENDING)
     * @return List of pending PTO requests for the department
     */
    @Query("SELECT p FROM PTORequest p WHERE p.employee.department = :department " +
        "AND p.status = :status")
    List<PTORequest> findByEmployeeDepartmentAndStatus(
        @Param("department") String department,
        @Param("status") PTOStatus status
    );

    /**
     * Count pending PTO requests for a manager's team
     * Used for: Dashboard notification badges
     * 
     * @param department The department name
     * @param status The PTO status (typically PENDING)
     * @return Count of pending PTO requests
     */
    long countByEmployeeDepartmentAndStatus(String department, PTOStatus status);

    // Queries by Leave Type

    /**
     * Find PTO requests by leave type (PTO, SICK, UNPAID)
     * Used for: Analyzing leave patterns by type
     * 
     * @param leaveType The type of leave
     * @return List of PTO requests of that type
     */
    List<PTORequest> findByLeaveType(LeaveType leaveType);

    /**
     * Find PTO requests for an employee by leave type
     * Used for: Employee leave balance tracking
     * 
     * @param employee The employee entity
     * @param leaveType The type of leave
     * @return List of PTO requests matching both criteria
     */
    List<PTORequest> findByEmployeeAndLeaveType(Employee employee, LeaveType leaveType);

    /**
     * Find approved requests by leave type for a date range
     * Used for: HR analytics, leave trend reporting
     * 
     * @param leaveType The type of leave
     * @param status The PTO status (APPROVED)
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of approved PTO requests
     */
    List<PTORequest> findByLeaveTypeAndStatusAndStartDateBetween(
        LeaveType leaveType,
        PTOStatus status,
        LocalDate startDate,
        LocalDate endDate
    );

    // Aggregation and Summary Queries

    /**
     * Get total days requested for an employee in a date range
     * Used for: Leave balance calculations, accrual tracking
     * 
     * @param employeeId The employee ID
     * @param status The PTO status (APPROVED)
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return Sum of days requested
     */
    @Query("SELECT COALESCE(SUM(p.daysRequested), 0) FROM PTORequest p " +
        "WHERE p.employee.id = :employeeId " +
        "AND p.status = :status " +
        "AND p.startDate BETWEEN :startDate AND :endDate")
    Double getTotalDaysTaken(
        @Param("employeeId") Long employeeId,
        @Param("status") PTOStatus status,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Count total PTO requests for an employee
     * Used for: Statistics, activity tracking
     * 
     * @param employeeId The employee ID
     * @return Count of PTO requests
     */
    long countByEmployeeId(Long employeeId);

    /**
     * Count pending requests for a specific employee
     * Used for: Employee dashboard (show pending approval)
     * 
     * @param employeeId The employee ID
     * @param status The PTO status (PENDING)
     * @return Count of pending requests
     */
    long countByEmployeeIdAndStatus(Long employeeId, PTOStatus status);

    // Reporting Queries

    /**
     * Find requests approved by a specific manager
     * Used for: Manager audit trails
     * 
     * @param managerId The manager's employee ID
     * @return List of PTO requests approved by this manager
     */
    List<PTORequest> findByApprovedById(Long managerId);

    /**
     * Find requests submitted after specific date
     * Used for: Recent activity reports
     * 
     * @param requestedAfter Date threshold
     * @return List of PTO requests submitted after the date
     */
    List<PTORequest> findByRequestedAtAfter(LocalDateTime requestedAfter);

    /**
     * Get all PTO requests for a department in a date range
     * Used for: Department-wide leave reports
     * 
     * @param department The department name
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of PTO requests
     */
    @Query("SELECT p FROM PTORequest p WHERE p.employee.department = :department " +
        "AND p.startDate BETWEEN :startDate AND :endDate")
    List<PTORequest> findByDepartmentAndDateRange(
        @Param("department") String department,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // Conflict detection

    /**
     * Check if an employee already has an approved request for a date range
     * Used for: Preventing double-booking leave
     * 
     * @param employeeId The employee ID
     * @param status The PTO status (APPROVED)
     * @param startDate Start of proposed leave
     * @param endDate End of proposed leave
     * @return True if conflicting request exists
     */
    @Query("SELECT COUNT(p) > 0 FROM PTORequest p " +
        "WHERE p.employee.id = :employeeId " +
        "AND p.status = :status " +
        "AND p.startDate <= :endDate AND p.endDate >= :startDate")
    boolean hasConflictingRequest(
        @Param("employeeId") Long employeeId,
        @Param("status") PTOStatus status,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}