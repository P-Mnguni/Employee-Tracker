package employee.tracker.controller;

import employee.tracker.dto.TimesheetRequest;
import employee.tracker.dto.TimesheetResponse;
import employee.tracker.mapper.TimesheetMapper;
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

    @Autowired
    private TimesheetMapper timesheetMapper;

    /**
     * Submit Timesheet Endpoint
     * POST /api/timesheets/submit
     * 
     * Input: TimesheetRequest (JSON body with employeeId, startDate, endDate)
     * 
     * @param request The timesheet submission request DTO
     * @return Success message with timesheet details or error
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitTimesheet(@RequestBody TimesheetRequest request) {
            
        Timesheet timesheet = timesheetService.submitTimesheet(
            request.getEmployeeId(),
            request.getStartDate(),
            request.getEndDate()
        );

        TimesheetResponse response = timesheetMapper.toResponse(timesheet);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Timesheet submitted successfully");
        result.put("timesheet", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
        
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
        
        Timesheet timesheet = timesheetService.approveTimesheet(timesheetId, managerId);
        TimesheetResponse response = timesheetMapper.toResponse(timesheet);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Timesheet approved successfully");
        result.put("timesheet", response);

        return ResponseEntity.ok(result);
        
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
        
        String rejectionReason = (reason != null && !reason.isEmpty()) ? reason : "No reason provided";

        Timesheet timesheet = timesheetService.rejectTimesheet(timesheetId, managerId, rejectionReason);
        TimesheetResponse response = timesheetMapper.toResponse(timesheet);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Timesheet rejected successfully");
        result.put("timesheet", response);

        return ResponseEntity.ok(result);
        
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
        
        List<Timesheet> timesheets = timesheetService.getEmployeeTimesheets(employeeId);
        List<TimesheetResponse> responses = timesheetMapper.toResponseList(timesheets);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("employeeId", employeeId);
        result.put("count", responses.size());
        result.put("timesheets", responses);

        return ResponseEntity.ok(result);
        
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
        
        List<Timesheet> timesheets = timesheetService.getEmployeeTimesheetsByDateRange(employeeId, startDate, endDate);
        List<TimesheetResponse> responses = timesheetMapper.toResponseList(timesheets);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("employeeId", employeeId);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("count", responses.size());
        result.put("timesheets", responses);

        return ResponseEntity.ok(result);
        
    }

    /**
     * Get All Pending Timesheets (Manager View)
     * GET /api/timesheets/pending
     * 
     * @return List of all pending timesheets
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getAllPendingTimesheets() {
        
        List<Timesheet> pendingTimesheets = timesheetService.getAllPendingTimesheets();
        List<TimesheetResponse> responses = timesheetMapper.toResponseList(pendingTimesheets);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", responses.size());
        result.put("pendingTimesheets", responses);

        return ResponseEntity.ok(result);
        
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
        
        List<Timesheet> pendingTimesheets = timesheetService.getPendingTimesheetsByDepartment(departmentName);
        List<TimesheetResponse> responses = timesheetMapper.toResponseList(pendingTimesheets);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("department", departmentName);
        result.put("count", responses.size());
        result.put("pendingTimesheet", responses);

        return ResponseEntity.ok(result);
        
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
        
        Timesheet timesheet = timesheetService.getTimesheetById(timesheetId);
        TimesheetResponse response = timesheetMapper.toResponse(timesheet);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("timesheet", response);
            
        return ResponseEntity.ok(result);
        
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
        
        long[] stats = timesheetService.getTimesheetStatistics(employeeId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("employeeId", employeeId);
        result.put("totalTimesheets", stats[0]);
        result.put("pendingTimesheets", stats[1]);
        result.put("approvedTimesheets", stats[2]);
        result.put("rejectedTimesheets", stats[3]);

        return ResponseEntity.ok(result);
        
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
        
        boolean exists = timesheetService.timesheetExistsForPeriod(employeeId, startDate, endDate);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("employeeId", employeeId);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("exists", exists);

        return ResponseEntity.ok(result);
        
    }
}
