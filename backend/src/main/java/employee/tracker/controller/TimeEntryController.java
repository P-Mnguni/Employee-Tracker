package employee.tracker.controller;

import employee.tracker.dto.ClockInRequest;
import employee.tracker.dto.ClockOutRequest;
import employee.tracker.dto.TimeEntryResponse;

import employee.tracker.mapper.TimeEntryMapper;
import employee.tracker.model.TimeEntry;
import employee.tracker.service.TimeEntryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * TimeEntryController - REST API endpoints for time tracking
 * 
 * This controller handles all HTTP requests related to clock-in/clock-out
 * operations and time entry management.
 * 
 * Base Route: /api/time
 */
@RestController
@RequestMapping("/api/time")
public class TimeEntryController {
    
    @Autowired
    private TimeEntryService timeEntryService;

    @Autowired
    private TimeEntryMapper timeEntryMapper;

    /**
     * Clock In Endpoint
     * POST /api/time/clock-in
     * 
     * Input: ClockInRequest (JSON body with employeeId and optional gpsLocation)
     * 
     * @param request The clock-in request DTO
     * @return Success message with entry details or error
     */
    @PostMapping("/clock-in")
    public ResponseEntity<?> clockIn(@RequestBody ClockInRequest request) {

        TimeEntry timeEntry = timeEntryService.clockIn(request.getEmployeeId());
        TimeEntryResponse response = timeEntryMapper.toResponse(timeEntry);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Clocked in successfully");
        result.put("entry", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);

    }

    /**
     * Clock Out Endpoint
     * POST /api/time/clock-out
     * 
     * Input: ClockOutRequest (JSON body with employeeId and optional fields)
     * 
     * @param request The clock-out request DTO
     * @return Success message with updated entry or error
     */
    @PostMapping("/clock-out")
    public ResponseEntity<?> clockOut(@RequestBody ClockOutRequest request) {

        TimeEntry timeEntry = timeEntryService.clockOut(request.getEmployeeId());
        TimeEntryResponse response = timeEntryMapper.toResponse(timeEntry);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Clocked out successfully");
        result.put("entry", response);

        return ResponseEntity.ok(result);
        
    }
    
    /**
     * Clock Out with Specific Time Endpoint (for corrections)
     * POST /api/time/clock-out-with-time
     * 
     * Input: employeeId and clockOutTime (request parameters for admin corrections)
     * 
     * @param employeeId The ID of the employee clocking out
     * @param clockOutTime The specific time to set as clock-out
     * @return Success message with updated entry or error
     */
    @PostMapping("/clock-out-with-time")
    public ResponseEntity<?> clockOutWithTime(@RequestParam Long employeeId, @RequestParam String clockOutTime) {
        
        LocalDateTime parsedTime = LocalDateTime.parse(clockOutTime);
        TimeEntry timeEntry = timeEntryService.clockOutWithTime(employeeId, parsedTime);
        TimeEntryResponse response = timeEntryMapper.toResponse(timeEntry);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Clocked out successfully with specified time");
        result.put("entry", response);

        return ResponseEntity.ok(result);
        
    }

    /**
     * Get All Time Entries for an Employee
     * GET /api/time/employee/{employeeId}
     * 
     * @param employeeId The ID of the employee (path variable)
     * @return List of time entries or error message
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getEmployeeEntries(@PathVariable Long employeeId) {
        
        List<TimeEntry> entries = timeEntryService.getEmployeeEntries(employeeId);
        List<TimeEntryResponse> responses = timeEntryMapper.toResponseList(entries);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("employeeId", employeeId);
        result.put("count", responses.size());
        result.put("entries", responses);

        return ResponseEntity.ok(result);
        
    }

    /**
     * Get Employee Entries by Date Range
     * GET /api/time/employee/{employeeId}/date-range?startDate=...&endDate=...
     * 
     * @param employeeId The ID of the employee
     * @param startDate Start of date range (ISO format: 2024-01-15T00:00:00)
     * @param endDate End of date range (ISO format: 2024-01-22T23:59:59)
     * @return List of time entries within date range
     */
    @GetMapping("/employee/{employeeId}/date-range")
    public ResponseEntity<?> getEmployeeEntriesByDateRange(
        @PathVariable Long employeeId,
        @RequestParam String startDate,
        @RequestParam String endDate
    ) {
        
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);

        List<TimeEntry> entries = timeEntryService.getEmployeeEntriesByDateRange(employeeId, start, end);
        List<TimeEntryResponse> responses = timeEntryMapper.toResponseList(entries);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("employeeId", employeeId);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("count", responses.size());
        result.put("entries", responses);

        return ResponseEntity.ok(result);
        
    }

    /**
     * Get current Active Session for an Employee
     * GET /api/time/employee/{employeeId}/active-session
     * 
     * @param employeeId The ID of the employee
     * @return Active session info or message if none
     */
    @GetMapping("/employee/{employeeId}/active-session")
    public ResponseEntity<?> getActiveSession(@PathVariable Long employeeId) {
        
        TimeEntry activeSession = timeEntryService.getActiveSession(employeeId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("employeeId", employeeId);
        result.put("isClockedIn", activeSession != null);

        if (activeSession != null) {
            TimeEntryResponse response = timeEntryMapper.toResponse(activeSession);
            result.put("activeEntryId", response);
        } else {
            result.put("message", "No active clock-in session found");
        }

        return ResponseEntity.ok(result);
        
    }

    /**
     * Check if Employee is Clocked In
     * GET /api/time/employee/{employeeId}/status
     * 
     * @param employeeId The ID of the employee
     * @return Status boolean
     */
    @GetMapping("/employee/{employeeId}/status")
    public ResponseEntity<?> isClockedIn(@PathVariable Long employeeId) {
        
        boolean isClockedIn = timeEntryService.isClockedIn(employeeId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("employeeId", employeeId);
        result.put("isClockedIn", isClockedIn);

        return ResponseEntity.ok(result);
        
    }

    /**
     * Get Today's Entries for an Employee
     * GET /api/time/employee/{employeeId}/today
     * 
     * @param employeeId The ID of the employee
     * @return Today's time entries
     */
    @GetMapping("/employee/{employeeId}/today")
    public ResponseEntity<?> getTodayEntries(@PathVariable Long employeeId) {
        
        List<TimeEntry> entries = timeEntryService.getTodayEntries(employeeId);
        List<TimeEntryResponse> responses = timeEntryMapper.toResponseList(entries);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("employeeId", employeeId);
        result.put("count", responses.size());
        result.put("entries", responses);

        return ResponseEntity.ok(result);
        
    }

    /**
     * Get All Pending Time Entries (Manager View)
     * GET /api/time/pending
     * 
     * @return List of all pending time entries
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getAllPendingEntries() {
        
        List<TimeEntry> pendingEntries = timeEntryService.getAllPendingEntries();
        List<TimeEntryResponse> responses = timeEntryMapper.toResponseList(pendingEntries);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", responses.size());
        result.put("pendingEntries", responses);

        return ResponseEntity.ok(result);
        
    }

    /**
     * Get Pending Entries for a Specific Employee
     * GET /api/time/employee/{employeeId}/pending
     * 
     * @param employeeId The ID of the employee
     * @return List of pending entries for the employee
     */
    @GetMapping("/employee/{employeeId}/pending")
    public ResponseEntity<?> getEmployeePendingEntries(@PathVariable Long employeeId) {
        
        List<TimeEntry> pendingEntries = timeEntryService.getEmployeePendingEntries(employeeId);
        List<TimeEntryResponse> responses = timeEntryMapper.toResponseList(pendingEntries);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("employeeId", employeeId);
        result.put("count", responses.size());
        result.put("pendingEntries", responses);

        return ResponseEntity.ok(result);
        
    }
}
