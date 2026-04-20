package employee.tracker.service;

import employee.tracker.model.Employee;
import employee.tracker.model.LeaveType;
import employee.tracker.model.PTORequest;
import employee.tracker.model.PTOStatus;

import employee.tracker.repository.EmployeeRepository;
import employee.tracker.repository.PTORequestRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * PTOService - PTO/Leave request business logic
 * 
 * This service handles the entire leave request lifecycle including:
 * - Creating/submitting PTO requests
 * - Manager approval/rejection of requests
 * - Conflict detection for overlapping requests
 */
@Service
@Transactional
public class PTOService {
    
    @Autowired
    private PTORequestRepository ptoRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Request PTO/Leave
     * 
     * Business Rules:
     * 1. Employee must exist
     * 2. Start date must be before end date
     * 3. Cannot request leave in the past
     * 4. Cannot request overlapping approved leave
     * 5. Request starts with PENDING status
     * 
     * @param employeeId The ID of the employee requesting leave
     * @param startDate Start date of the leave
     * @param endDate End date of the leave
     * @param leaveType Type of leave
     * @param reason Reason for the leave request (optional)
     * @return The created PTORequest
     * @throws RuntimeException if validation fails
     */
    public PTORequest requestPTO(Long employeeId, LocalDate startDate, LocalDate endDate, LeaveType leaveType, String reason) {
        Employee employee = employeeRepository.findById(employeeId)
                            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        LocalDate today = LocalDate.now();
        if (startDate.isBefore(today)) {
            throw new IllegalStateException("Cannot request leave for past dates");
        }

        boolean hasConflict = ptoRequestRepository.hasConflictingRequest(employeeId, PTOStatus.APPROVED, startDate, endDate);
        if (hasConflict) {
            throw new IllegalStateException("You already have an approved PTO request that overlaps with these dates. "
                + "Please check your existing requests.");
        }

        PTORequest request = new PTORequest(employee, startDate, endDate, leaveType, reason);
        request.setStatus(PTOStatus.PENDING);
        return ptoRequestRepository.save(request);
    }

    /**
     * Request PTO with partial day support
     * 
     * @param employeeId The ID of the employee requesting leave
     * @param startDate Start date of the leave
     * @param endDate End date of the leave
     * @param leaveType Type of leave
     * @param isPartialDay Whether this is a partial day
     * @param daysRequested Number of days 
     * @return The created PTORequest
     */
    public PTORequest requestPTOWithPartial(Long employeeId, LocalDate startDate, LocalDate endDate, LeaveType leaveType,
                                            String reason,Boolean isPartialDay, Double daysRequested) {
        
        PTORequest request = requestPTO(employeeId, startDate, endDate, leaveType, reason);
        if (isPartialDay != null) {
            request.setIsPartialDay(isPartialDay);
        }
        if (daysRequested != null) {
            request.setDaysRequested(daysRequested);
        }

        return ptoRequestRepository.save(request);
    }

    /**
     * Approve a PTO request
     * 
     * Business Rules:
     * 1. Request must exist
     * 2. Request must be in PENDING status
     * 3. Only managers should be allowed (role check in controller layer)
     * 4. Check for conflicts with other approved requests before approving
     * 
     * @param requestId The ID of the PTO request to approve
     * @param managerID The ID of the manager approving the request
     * @return The approved PTORequest
     * @throws RuntimeException if validation fails
     */
    public PTORequest approvePTO(Long requestId, Long managerId) {
        PTORequest request = ptoRequestRepository.findById(requestId)
                                .orElseThrow(() -> new RuntimeException("PTO request not found with id: " + requestId));

        if (request.getStatus() != PTOStatus.PENDING) {
            throw new IllegalStateException("Cannot approve PTO request with status: " + request.getStatus() + 
            ". Only PENDING requests can be approved.");
        }

        Employee manager = employeeRepository.findById(managerId)
                            .orElseThrow(() -> new RuntimeException("Manager not found with id: " + managerId));

        boolean hasConflict = ptoRequestRepository.hasConflictingRequest(
                                                                        request.getEmployee().getId(), 
                                                                        PTOStatus.APPROVED, 
                                                                        request.getStartDate(), 
                                                                        request.getEndDate()
                                                                    );
        
        if (hasConflict) {
            throw new IllegalStateException("Cannot approve: Employee already has an approved " + 
                                            "PTO request that overlaps with these dates.");
        }

        request.approve(manager);   
        return ptoRequestRepository.save(request);
    }

    /**
     * Reject a PTO request
     * 
     * Business Rules:
     * 1. Request must exist
     * 2. Request must be in PENDING status
     * 3. Only managers should be allowed 
     * 
     * @param requestId The ID of the PTO request to reject
     * @param managerId The ID of the manager rejecting the request
     * @param reason The reason for rejection
     * @return The rejected PTORequest
     * @throws RuntimeException if validation fails
     */
    public PTORequest rejectPTO(Long requestId, Long managerId, String reason) {
        PTORequest request = ptoRequestRepository.findById(requestId)
                                .orElseThrow(() -> new RuntimeException("PTO request not found with id: " + requestId));
            
        if (request.getStatus() != PTOStatus.PENDING) {
            throw new IllegalStateException("Cannot reject PTO request with status: " + request.getStatus() +
                                            ". Only PENDING requests can be rejected.");
        }

        Employee manager = employeeRepository.findById(managerId)
                            .orElseThrow(() -> new RuntimeException("Manager not found with id: " + managerId));

        request.reject(manager, reason);    
        return ptoRequestRepository.save(request);
    }

    /**
     * Cancel a PTO request (employee withdraws before approval)
     * 
     * Business Rules:
     * 1. Request must exist
     * 2. Request must be in PENDING status
     * 3. Only the requesting employee or admin can cancel
     * 
     * @param requestId The ID of the PTO request to cancel
     * @return The cancelled PTORequest
     */
    public PTORequest cancelPTORequest(Long requestId) {
        PTORequest request = ptoRequestRepository.findById(requestId)
                                .orElseThrow(() -> new RuntimeException("PTO request not found with id: " + requestId));

        if (request.getStatus() != PTOStatus.PENDING) {
            throw new IllegalStateException("Cannot cancel PTO request with status: " + request.getStatus() +
                                            ". Only PENDING requests can be cancelled.");
        }

        request.cancel();
        return ptoRequestRepository.save(request);
    }

    /**
     * Get all PTO requests for an employee
     * 
     * @param employeeId The ID of the employee
     * @return List of all PTO requests for the employee
     * @throws RuntimeException if employee not found
     */
    public List<PTORequest> getEmployeeRequests(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new RuntimeException("Employee not found with id: " + employeeId);
        }

        return ptoRequestRepository.findByEmployeeId(employeeId);
    }

    /**
     * Get PTO requests for an employee filtered by status
     * 
     * @param employeeId The ID of the employee
     * @param status The status to filter by (PENDING, APPROVED, REJECTED)
     * @return List of filtered PTO request
     */
    public List<PTORequest> getEmployeeRequestsByStatus(Long employeeId, PTOStatus status) {
        Employee employee = employeeRepository.findById(employeeId)
                            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        return ptoRequestRepository.findByEmployeeAndStatus(employee, status);
    }

    /**
     * Get PTO requests for an employee by leave type
     * 
     * @param employeeId The ID of the employee
     * @param leaveType The type of leave
     * @return List of PTO requests of that type
     */
    public List<PTORequest> getEmployeeRequestsByType(Long employeeId, LeaveType leaveType) {
        Employee employee = employeeRepository.findById(employeeId)
                            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        return ptoRequestRepository.findByEmployeeAndLeaveType(employee, leaveType);
    }

    /**
     * Get all pending PTO requests (for manager approval dashboard)
     * 
     * @return List of all pending PTO requests across all employees
     */
    public List<PTORequest> getAllPendingRequests() {
        return ptoRequestRepository.findByStatus(PTOStatus.PENDING);
    }

    /**
     * Get pending PTO requests for a specific department (for department managers)
     * 
     * @param department The department name
     * @return List of pending PTO requests for employees in the department
     */
    public List<PTORequest> getPendingRequestsByDepartment(String department) {
        return ptoRequestRepository.findByEmployeeDepartmentAndStatus(department, PTOStatus.PENDING);
    }

    /**
     * Get PTO requests within a date range (for reporting)
     * 
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of PTO requests in the date range
     */
    public List<PTORequest> getRequestsByDateRange(LocalDate startDate, LocalDate endDate) {
        return ptoRequestRepository.findByStartDateBetween(startDate, endDate);
    }

    /**
     * Get total PTO days taken by an employee in a year
     * 
     * @param employeeId The ID of the employee
     * @param year The year to calculate for 
     * @return Total days of approved PTO taken
     */
    public double getTotalPTODaysTaken(Long employeeId, int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);

        return ptoRequestRepository.getTotalDaysTaken(
            employeeId,
            PTOStatus.APPROVED, 
            startOfYear, 
            endOfYear
        );
    }

    /**
     * Get PTO request by ID
     * 
     * @param requestId The ID of the request
     * @return The PTORequest entity
     */
    public PTORequest getRequestByID(Long requestId) {
        return ptoRequestRepository.findById(requestId)
                                    .orElseThrow(() -> new RuntimeException("PTO request not found with id: " + requestId));
    }

    /**
     * Check if an employee has any pending PTO requests
     * 
     * @param employeeId The ID of the employee
     * @return true if employee has pending requests, false otherwise
     */
    public boolean hasPendingRequests(Long employeeId) {
        long pendingCount = ptoRequestRepository.countByEmployeeIdAndStatus(employeeId, PTOStatus.PENDING);
        return pendingCount > 0;
    }

    /**
     * Get PTO statistics for an employee
     * 
     * @param employeeId The ID of the employee
     * @return Array of counts: [total, pending, approved, rejected]
     */
    public long[] getPTOStatistics(Long employeeId) {
        List<PTORequest> requests = getEmployeeRequests(employeeId);

        long total = requests.size();
        long pending = requests.stream().filter(r -> r.getStatus() == PTOStatus.PENDING).count();
        long approved = requests.stream().filter(r -> r.getStatus() == PTOStatus.APPROVED).count();
        long rejected = requests.stream().filter(r -> r.getStatus() == PTOStatus.REJECTED).count();

        return new long[]{total, pending, approved, rejected};
    }

    /**
     * Check for overlapping PTO requests (utility method)
     * 
     * @param employeeId The ID of the employee
     * @param startDate Proposed start date
     * @param endDate Proposed end date
     * @return true if conflict exists with approved requests
     */
    public boolean hasOverlappingRequest(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return ptoRequestRepository.hasConflictingRequest(employeeId, PTOStatus.APPROVED, startDate, endDate);
    }
}
