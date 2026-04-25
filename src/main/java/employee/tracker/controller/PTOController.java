package employee.tracker.controller;

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

    /**
     * Request PTO Endpoint
     * POST /api/pto/request
     * 
     * Input: employeeId, startDate, endDate, type, reason
     * 
     * @param employeeId The ID of the employee requesting leave
     * @param startDate Start date of leave (format: yyyy-MM-dd)
     * @param endDate End date of leave
     * @param type Type of leave (PTO, SICK, UNPAID)
     * @param reason Reason for leave
     * @return Success message with request details or error
     */
    @PostMapping("/request")
    public ResponseEntity<?> requestPTO(
        @RequestParam Long employeeId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam LeaveType type,
        @RequestParam(required = false) String reason
    ) {
        try {
            PTORequest request = ptoService.requestPTO(employeeId, startDate, endDate, type, reason);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "PTO request submitted successfully");
            response.put("requestId", request.getId());
            response.put("employeeId", employeeId);
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("leaveType", type);
            response.put("status", request.getStatus());
            response.put("daysRequested", request.getDaysRequested());
            response.put("requestedAt", request.getRequestedAt());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Request PTO with Partial Day Support
     * POST /api/pto/request-with-partial
     * 
     * @param employeeId The ID of the employee requesting leave
     * @param startDate Start date of leave
     * @param endDate End date of leave
     * @param type Type of leave
     * @param reason Reason for leave
     * @param isPartialDay Whether this is a partial day request
     * @param daysRequested Number of days (for partial day, e.g., 0.5)
     * @return Success message with request details
     */
    @PostMapping("/request-with-partial")
    public ResponseEntity<?> requestPTOWithPartial(
        @RequestParam Long employeeId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam LeaveType type,
        @RequestParam(required = false) String reason,
        @RequestParam(required = false) Boolean isPartialDay,
        @RequestParam(required = false) Double daysRequested
    ) {
        try {
            PTORequest request = ptoService.requestPTOWithPartial(employeeId, startDate, endDate, type, reason, isPartialDay, daysRequested);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "PTO request submitted successfully");
            response.put("requestId", request.getId());
            response.put("employeeId", employeeId);
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("leaveType", type);
            response.put("status", request.getStatus());
            response.put("isPartialDay", request.getIsPartialDay());
            response.put("daysRequested", request.getDaysRequested());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
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
        try {
            PTORequest request = ptoService.approvePTO(requestId, managerId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "PTO request approved successfully");
            response.put("requestId", request.getId());
            response.put("employeeId", request.getEmployee().getId());
            response.put("employeeName", request.getEmployee().getName());
            response.put("startDate", request.getStartDate());
            response.put("endDate", request.getEndDate());
            response.put("leaveType", request.getLeaveType());
            response.put("status", request.getStatus());
            response.put("approvedBy", request.getApprovedBy().getName());
            response.put("approvedAt", request.getApprovedAt());
            response.put("daysRequested", request.getDaysRequested());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());

            HttpStatus status = e.getMessage().contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(error);
        }
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
        try {
            String rejectionReason = (reason != null && !reason.isEmpty()) ? reason : "No reason provided";

            PTORequest request = ptoService.rejectPTO(requestId, managerId, rejectionReason);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "PTO request rejected successfully");
            response.put("requestId", request.getId());
            response.put("employeeId", request.getEmployee().getId());
            response.put("employeeName", request.getEmployee().getName());
            response.put("startDate", request.getStartDate());
            response.put("endDate", request.getEndDate());
            response.put("leaveType", request.getLeaveType());
            response.put("status", request.getStatus());
            response.put("rejectedBy", request.getApprovedBy().getName());
            response.put("rejectedAt", request.getApprovedAt());
            response.put("rejectionReason", request.getRejectionReason());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());

            HttpStatus status = e.getMessage().contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(error);
        }
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
        try {
            PTORequest request = ptoService.cancelPTORequest(requestId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "PTO request cancelled successfully");
            response.put("requestId", request.getId());
            response.put("status", request.getStatus());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Get All Requests for an Employee
     * GET /api/pto/employee/{employeeId}
     * 
     * @param employeeId The ID of the employee
     * @return List of PTO requests or error message
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getEmployeeRequests(@PathVariable Long employeeId) {
        try {
            List<PTORequest> requests = ptoService.getEmployeeRequests(employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("employeeId", employeeId);
            response.put("count", requests.size());
            response.put("requests", requests);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
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
        try {
            List<PTORequest> requests = ptoService.getEmployeeRequestsByStatus(employeeId, status);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("employeeId", employeeId);
            response.put("status", status);
            response.put("count", requests.size());
            response.put("requests", requests);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
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
        try {
            List<PTORequest> requests = ptoService.getEmployeeRequestsByType(employeeId, leaveType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("employeeId", employeeId);
            response.put("leaveType", leaveType);
            response.put("count", requests.size());
            response.put("requests", requests);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Get All Pending PTO Requests (Manager Dashboard)
     * GET /api/pto/pending
     * 
     * @return List of all pending PTO requests
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getAllPendingRequests() {
        try {
            List<PTORequest> pendingRequests = ptoService.getAllPendingRequests();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", pendingRequests.size());
            response.put("pendingRequests", pendingRequests);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
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
        try {
            List<PTORequest> pendingRequests = ptoService.getPendingRequestsByDepartment(departmentName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("department", departmentName);
            response.put("count", pendingRequests.size());
            response.put("pendingRequests", pendingRequests);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
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
        try {
            PTORequest request = ptoService.getRequestByID(requestId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("request", request);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
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
        try {
            double totalDays = ptoService.getTotalPTODaysTaken(employeeId, year);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("employeeId", employeeId);
            response.put("year", year);
            response.put("totalDaysTaken", totalDays);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
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
        try {
            long[] stats = ptoService.getPTOStatistics(employeeId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("employeeId", employeeId);
            response.put("totalRequests", stats[0]);
            response.put("pendingRequests", stats[1]);
            response.put("approvedRequests", stats[2]);
            response.put("rejectedRequests", stats[3]);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
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
        try {
            boolean hasConflict = ptoService.hasOverlappingRequest(employeeId, startDate, endDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("employeeId", employeeId);
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("hasConflict", hasConflict);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
