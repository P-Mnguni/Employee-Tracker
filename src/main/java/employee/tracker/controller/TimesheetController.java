package employee.tracker.controller;

import employee.tracker.model.Timesheet;
import employee.tracker.service.TimesheetService;
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
 * TimesheetController - REST API endpoints for timesheet management
 * 
 * This controller handles all HTTP requests related to timesheet submission,
 * approval, rejection, and retrieval.
 * 
 * Base Route: /api/timesheets
 */
@RestController
@RequestMapping("/api/timesheets")
public class TimesheetController {
    
    @Autowired
    private TimesheetService timesheetService;

    /**
     * Submit Timesheet Endpoint
     * POST /api/timesheets/submit
     * 
     * Input: employeeId, startDate, endDate
     * 
     * @param employeeId The ID of the employee submitting the timesheet
     * @param startDate Start date of the pay period (format: yyyy-MM-dd)
     * @param endDate End date of the pay period
     * @return Success message with timesheet details or error
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitTimesheet(
        @RequestParam Long employeeId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        try {
            Timesheet timesheet = timesheetService.submitTimesheet(employeeId, startDate, endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Timesheet submitted successfully");
            response.put("timesheetId", timesheet.getId());
            response.put("employeeId", employeeId);
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("status", timesheet.getStatus());
            response.put("submittedAt", timesheet.getSubmittedAt());
            response.put("entryCount", timesheet.getTimeEntries().size());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Approve Timesheet Endpoint
     * PUT /api/timesheets/{timesheetId}/approve
     * 
     * Input: timesheetId (path variable), managerId (request param)
     * 
     * @param timesheetId The ID of the timesheet to approve
     * @param managerId The ID of the manager approving the timesheet
     * @return Success message with approved timesheet details or error
     */
    @PutMapping("/{timesheetId}/approve")
    public ResponseEntity<?> approveTimesheet(
        @PathVariable Long timesheetId,
        @RequestParam Long managerId
    ) {
        try {
            Timesheet timesheet = timesheetService.approveTimesheet(timesheetId, managerId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Timesheet approved successfully");
            response.put("timesheetId", timesheet.getId());
            response.put("employeeId", timesheet.getEmployee().getId());
            response.put("employeeName", timesheet.getEmployee().getName());
            response.put("status", timesheet.getStatus());
            response.put("approvedBy", timesheet.getApprovedBy().getName());
            response.put("approvedAt", timesheet.getApprovedAt());
            response.put("startDate", timesheet.getStartDate());
            response.put("endDate", timesheet.getEndDate());
            response.put("entryCount", timesheet.getTimeEntries().size());

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
     * Reject Timesheet Endpoint
     * PUT /api/timesheets/{timesheetId}/reject
     * 
     * Input: timesheetId (path variable), managerId, reason (request params)
     * 
     * @param timesheetId The ID of the timesheet to reject
     * @param managerId The ID of the manager rejecting the timesheet
     * @param reason The reason for rejection
     * @return Success message with rejected timesheet details or error
     */
    @PutMapping("/{timesheetId}/reject")
    public ResponseEntity<?> rejectTimesheet(
        @PathVariable Long timesheetId,
        @RequestParam Long managerId,
        @RequestParam(required = false) String reason
    ) {
        try {
            String rejectionReason = (reason != null && !reason.isEmpty()) ? reason : "No reason provided";

            Timesheet timesheet = timesheetService.rejectTimesheet(timesheetId, timesheetId, rejectionReason);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Timesheet rejected successfully");
            response.put("timesheetId", timesheet.getId());
            response.put("employeeId", timesheet.getEmployee().getId());
            response.put("employeeName", timesheet.getEmployee().getName());
            response.put("status", timesheet.getStatus());
            response.put("rejectedBy", timesheet.getApprovedBy().getName());
            response.put("rejectedAt", timesheet.getApprovedAt());
            response.put("rejectionReason", timesheet.getRejectionReason());
            response.put("startDate", timesheet.getStartDate());
            response.put("endDate", timesheet.getEndDate());

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
     * Get All Timesheets for an Employee
     * GET /api/timesheets/employee/{employeeId}
     * 
     * @param employeeId The ID of the employee
     * @return List of timesheets or error message
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getEmployeeTimesheets(@PathVariable Long employeeId) {
        try {
            List<Timesheet> timesheets = timesheetService.getEmployeeTimesheets(employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("employeeId", employeeId);
            response.put("count", timesheets.size());
            response.put("timesheets", timesheets);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Get Timesheets by Date Range for an Employee
     * GET /api/timesheets/employee/{employeeId}/date-range?startDate=...&endDate=...
     * 
     * @param employeeId The ID of te employee
     * @param startDate Start of date range (format: yyyy-MM-dd)
     * @param endDate End of date range
     * @return List of timesheets within date range
     */
    @GetMapping("/employee/{employeeId}/date-range")
    public ResponseEntity<?> getEmployeeTimesheetsByDateRange(
        @PathVariable Long employeeId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        try {
            List<Timesheet> timesheets = timesheetService.getEmployeeTimesheetsByDateRange(employeeId, startDate, endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("employeeId", employeeId);
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("count", timesheets.size());
            response.put("timesheets", timesheets);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Get All Pending Timesheets (Manager View)
     * GET /api/timesheets/pending
     * 
     * @return List of all pending timesheets
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getAllPendingTimesheets() {
        try {
            List<Timesheet> pendingTimesheets = timesheetService.getAllPendingTimesheets();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", pendingTimesheets.size());
            response.put("pendingTimesheets", pendingTimesheets);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get Pending Timesheets by Department (Department Manager View)
     * GET ?api/timesheets/pending/department?departmentName=...
     * 
     * @param departmentName The name of the department
     * @return List of pending timesheets for that department
     */
    @GetMapping("/pending/department")
    public ResponseEntity<?> getPendingTimesheetsByDepartment(@RequestParam String departmentName) {
        try {
            List<Timesheet> pendingTimesheets = timesheetService.getPendingTimesheetsByDepartment(departmentName);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("department", departmentName);
            response.put("count", pendingTimesheets.size());
            response.put("pendingTimesheet", pendingTimesheets);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Get Timesheet by ID
     * GET /api/timesheets/{timesheetId}
     * 
     * @param timesheetId The ID of the timesheet
     * @return Timesheet details
     */
    @GetMapping("/{timesheetId}")
    public ResponseEntity<?> getTimesheetById(@PathVariable Long timesheetId) {
        try {
            Timesheet timesheet = timesheetService.getTimesheetById(timesheetId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("timesheet", timesheet);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Get Timesheet Statistics for an Employee
     * GET /api/timesheets/employee/{employeeId}/statistics
     * 
     * @param employeeId The ID of the employee
     * @return Statistics counts [total, pending, approved, rejected]
     */
    @GetMapping("/employee/{employeeId}/statistics")
    public ResponseEntity<?> getTimesheetStatistics(@PathVariable Long employeeId) {
        try {
            long[] stats = timesheetService.getTimesheetStatistics(employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("employeeId", employeeId);
            response.put("totalTimesheets", stats[0]);
            response.put("pendingTimesheets", stats[1]);
            response.put("approvedTimesheets", stats[2]);
            response.put("rejectedTimesheets", stats[3]);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Check if Timesheet Exists for a Period
     * GET /api/timesheets/exists?employeeId=...&startDate...&endDate=...
     * 
     * @param employeeId The ID of the employee
     * @param startDate Start of pay period
     * @param endDate End of pay period
     * @return Boolean indicating if timesheet exists
     */
    @GetMapping("/exists")
    public ResponseEntity<?> timesheetExistsForPeriod(
        @RequestParam Long employeeId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        try {
            boolean exists = timesheetService.timesheetExistsForPeriod(employeeId, startDate, endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("employeeId", employeeId);
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("exists", exists);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
