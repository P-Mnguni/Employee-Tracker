package employee.tracker.controller;

import employee.tracker.dto.PTORequestDTO;
import employee.tracker.dto.PTOResponse;
import employee.tracker.mapper.PTOMapper;

import employee.tracker.model.PTORequest;
import employee.tracker.model.PTOStatus;
import employee.tracker.model.LeaveType;
import employee.tracker.service.PTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PTOController - REST API endpoints for PTO/Leave request management
 * 
 * This controller handles all HTTP requests related to leave requests,
 * including requesting PTO, approving, rejecting, and viewing requests.
 * 
 * Base Route: /api/pto
 */
@RestController
@RequestMapping("/api/pto")
public class PTOController {
    
    @Autowired
    private PTOService ptoService;

    @Autowired
    private PTOMapper ptoMapper;

    /**
     * Request PTO Endpoint
     * POST /api/pto/request
     * 
     * Input: PTORequestDTO (JSON body with employeeId, dates, type, reason)
     * 
     * @param request The PTO request PTO
     * @return Success message with request details or error
     */
    @PostMapping("/request")
    public ResponseEntity<?> requestPTO(@RequestBody PTORequestDTO request) {

        PTORequest ptoRequest;

        // Handle partial day requests
        if(request.getIsPartialDay() != null && request.getIsPartialDay()) {
            ptoRequest = ptoService.requestPTOWithPartial(
                request.getEmployeeId(), 
                request.getStartDate(), 
                request.getEndDate(), 
                request.getType(), 
                request.getReason(), 
                request.getIsPartialDay(), 
                request.getDaysRequested()
            );
        } else {
            ptoRequest = ptoService.requestPTO(
                request.getEmployeeId(),
                request.getStartDate(),
                request.getEndDate(),
                request.getType(),
                request.getReason()
            );
        }

        PTOResponse response = ptoMapper.toResponse(ptoRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "PTO request submitted successfully");
        result.put("request", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
        
    }

    /**
     * Approve PTO Request Endpoint
     * PUT /api/pto/{requestId}/approve
     * 
     * Input: requestId (path variable), managerId (request param)
     * 
     * @param requestId The ID of the PTO request to approve
     * @param managerId The ID of the manager approving the request
     * @return Success message with approved request details of error
     */
    @PutMapping("/{requestId}/approve")
    public ResponseEntity<?> approvePTO(@PathVariable Long requestId, @RequestParam Long managerId) {
        
        PTORequest request = ptoService.approvePTO(requestId, managerId);
        PTOResponse response = ptoMapper.toResponse(request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "PTO request approved successfully");
        result.put("request", response);

        return ResponseEntity.ok(result);
        
    }

    /**
     * Reject PTO Request Endpoint
     * PUT /api/pto/{requestId}/reject
     * 
     * Input: requestId (path variable), managerId, reason (request params)
     * 
     * @param requestId The ID of the PTO request to reject
     * @param managerId The ID of the manager rejecting the request
     * @param reason The reason for rejection
     * @return Success message with rejected request details or error
     */
    @PutMapping("/{requestId}/reject")
    public ResponseEntity<?> rejectPTO(
        @PathVariable Long requestId,
        @RequestParam Long managerId,
        @RequestParam(required = false) String reason
    ) {
        
        String rejectionReason = (reason != null && !reason.isEmpty()) ? reason : "No reason provided";

        PTORequest request = ptoService.rejectPTO(requestId, managerId, rejectionReason);
        PTOResponse response = ptoMapper.toResponse(request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "PTO request rejected successfully");
        result.put("request", response);

        return ResponseEntity.ok(result);
        
    }

    /**
     * Cancel PTO Request Endpoint (Employee withdraws before approval)
     * PUT /api/pto/{requestId}/cancel
     * 
     * @param requestId The ID of the PTO request to cancel
     * @return Success message
     */
    @PutMapping("/{requestId}/cancel")
    public ResponseEntity<?> cancelPTORequest(@PathVariable Long requestId) {
        
        PTORequest request = ptoService.cancelPTORequest(requestId);
        PTOResponse response = ptoMapper.toResponse(request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "PTO request cancelled successfully");
        result.put("requestId", response);

        return ResponseEntity.ok(result);
        
    }

    /**
     * Get All PTO Requests for an Employee
     * GET /api/pto/employee/{employeeId}
     * 
     * @param employeeId The ID of the employee
     * @return List of PTO requests or error message
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getEmployeeRequests(@PathVariable Long employeeId) {
        
        List<PTORequest> requests = ptoService.getEmployeeRequests(employeeId);
        List<PTOResponse> responses = ptoMapper.toResponseList(requests);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("employeeId", employeeId);
        result.put("count", responses.size());
        result.put("requests", responses);

        return ResponseEntity.ok(result);
        
    }

    /**
     * Get Employee Requests by Status
     * GET /api/pto/employee/{employeeId}/status?status=PENDING
     * 
     * @param employeeId The ID of the employee
     * @param status The status to filter by (PENDING, APPROVED, REJECTED)
     * @return List of filtered PTO requests
     */
    @GetMapping("/employee/{employeeId}/status")
    public ResponseEntity<?> getEmployeeRequestsByStatus(@PathVariable Long employeeId, @RequestParam PTOStatus status) {
    
        List<PTORequest> requests = ptoService.getEmployeeRequestsByStatus(employeeId, status);
        List<PTOResponse> responses = ptoMapper.toResponseList(requests);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("employeeId", employeeId);
        result.put("status", status);
        result.put("count", responses.size());
        result.put("requests", responses);

        return ResponseEntity.ok(result);
        
    }

    /**
     * Get Employee Requests by Leave Type
     * GET /api/pto/employee/{employeeId}/type?leaveType=PTO
     * 
     * @param employeeId The ID of the employee
     * @param leaveType The leave type to filter by
     * @return List of filtered PTO requests
     */
    @GetMapping("/employee/{employeeId}/type")
    public ResponseEntity<?> getEmployeeRequestsByType(
        @PathVariable Long employeeId,
        @RequestParam LeaveType leaveType
    ) {
        
        List<PTORequest> requests = ptoService.getEmployeeRequestsByType(employeeId, leaveType);
        List<PTOResponse> responses = ptoMapper.toResponseList(requests);
            
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("employeeId", employeeId);
        result.put("leaveType", leaveType);
        result.put("count", responses.size());
        result.put("requests", responses);
            
        return ResponseEntity.ok(result);
        
    }

    /**
     * Get All Pending PTO Requests (Manager Dashboard)
     * GET /api/pto/pending
     * 
     * @return List of all pending PTO requests
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getAllPendingRequests() {
        
        List<PTORequest> pendingRequests = ptoService.getAllPendingRequests();
        List<PTOResponse> responses = ptoMapper.toResponseList(pendingRequests);
            
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", responses.size());
        result.put("pendingRequests", responses);
            
        return ResponseEntity.ok(result);
        
    }

    /**
     * Get Pending Requests by Department (Department Manager View)
     * GET /api/pto/pending/department?departmentName=Engineering
     * 
     * @param departmentName The name of the department
     * @return List of pending PTO requests for that department
     */
    @GetMapping("/pending/department")
    public ResponseEntity<?> getPendingRequestsByDepartment(@RequestParam String departmentName) {
        
        List<PTORequest> pendingRequests = ptoService.getPendingRequestsByDepartment(departmentName);
        List<PTOResponse> responses = ptoMapper.toResponseList(pendingRequests);
            
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("department", departmentName);
        result.put("count", responses.size());
        result.put("pendingRequests", responses);
            
        return ResponseEntity.ok(result);
        
    }

    /**
     * Get PTO Request by ID
     * GET /api/pto/{requestId}
     * 
     * @param requestId The ID of the PTO request
     * @return PTO request details
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<?> getRequestById(@PathVariable Long requestId) {
        
        PTORequest request = ptoService.getRequestByID(requestId);
        PTOResponse response = ptoMapper.toResponse(request);
            
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("request", response);
            
        return ResponseEntity.ok(result);
        
    }

    /**
     * Get Total PTO Days Taken by Employee in a Year
     * GET /api/pto/employee/{employeeId}/balance?year=2024
     * 
     * @param employeeId The ID of the employee
     * @param year The year to calculate for
     * @return Total PTO days taken
     */
    @GetMapping("/employee/{employeeId}/balance")
    public ResponseEntity<?> getTotalPTODaysTaken(@PathVariable Long employeeId,
                                                   @RequestParam int year) {
        
        double totalDays = ptoService.getTotalPTODaysTaken(employeeId, year);
            
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("employeeId", employeeId);
        result.put("year", year);
        result.put("totalDaysTaken", totalDays);
            
        return ResponseEntity.ok(result);
        
    }

    /**
     * Get PTO Statistics for an Employee
     * GET /api/pto/employee/{employeeId}/statistics
     * 
     * @param employeeId The ID of the employee
     * @return Statistics counts [total, pending, approved, rejected]
     */
    @GetMapping("/employee/{employeeId}/statistics")
    public ResponseEntity<?> getPTOStatistics(@PathVariable Long employeeId) {
        
        long[] stats = ptoService.getPTOStatistics(employeeId);
            
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("employeeId", employeeId);
        result.put("totalRequests", stats[0]);
        result.put("pendingRequests", stats[1]);
        result.put("approvedRequests", stats[2]);
        result.put("rejectedRequests", stats[3]);
            
        return ResponseEntity.ok(result);
        
    }

    /**
     * Check if Employee Has Overlapping PTO Request
     * GET /api/pto/employee/{employeeId}/has-conflict?startDate=...&endDate=...
     * 
     * @param employeeId The ID of the employee
     * @param startDate Proposed start date
     * @param endDate Proposed end date
     * @return Boolean indicating if conflict exists
     */
    @GetMapping("/employee/{employeeId}/has-conflict")
    public ResponseEntity<?> hasOverlappingRequest(@PathVariable Long employeeId,
                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        boolean hasConflict = ptoService.hasOverlappingRequest(employeeId, startDate, endDate);
            
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("employeeId", employeeId);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("hasConflict", hasConflict);
        
        return ResponseEntity.ok(result);
        
    }
}
