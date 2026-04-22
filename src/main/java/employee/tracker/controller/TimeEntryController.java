package employee.tracker.controller;

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

    /**
     * Clock In Endpoint
     * POST /api/time/clock-in
     * 
     * Input: employeeId (request parameter or JSON body)
     * 
     * @param employeeId The ID of the employee clocking in
     * @return Success message with entry details or error
     */
    @PostMapping("/clock-in")
    public ResponseEntity<?> clockIn(@RequestParam Long employeeId) {
        try {
            TimeEntry timeEntry = timeEntryService.clockIn(employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Clocked in successfully");
            response.put("entryId", timeEntry.getId());
            response.put("clockInTime", timeEntry.getClockInTime());
            response.put("employeeId", employeeId);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Clock Out Endpoint
     * POST /api/time/clock-out
     * 
     * Input: employeeId (request parameter or JSON body)
     * 
     * @param employeeId The ID of the employee clocking out
     * @return Success message with updated entry or error
     */
    @PostMapping("/clock-out")
    public ResponseEntity<?> clockOut(@RequestParam Long employeeId) {
        try {
            TimeEntry timeEntry = timeEntryService.clockOut(employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Clocked out successfully");
            response.put("entryId", timeEntry.getId());
            response.put("clockInTime", timeEntry.getClockInTime());
            response.put("clockOutTime", timeEntry.getClockOutTime());
            response.put("employeeId", employeeId);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * Clock Out with Specific Time Endpoint (for corrections)
     * POST /api/time/clock-out-with-time
     * 
     * Input: employeeId and clockOutTime (request parameters)
     * 
     * @param employeeId The ID of the employee clocking out
     * @param clockOutTime The specific time to set as clock-out
     * @return Success message with updated entry or error
     */
    @PostMapping("/clock-out-with-time")
    public ResponseEntity<?> clockOutWithTime(@RequestParam Long employeeId, @RequestParam String clockOutTime) {
        try {
            LocalDateTime parsedTime = LocalDateTime.parse(clockOutTime);
            TimeEntry timeEntry = timeEntryService.clockOutWithTime(employeeId, parsedTime);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Clocked out successfully with specified time");
            response.put("entryId", timeEntry.getId());
            response.put("clockInTime", timeEntry.getClockInTime());
            response.put("clockOutTime", timeEntry.getClockOutTime());
            response.put("employeeId", employeeId);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
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
        try {
            List<TimeEntry> entries = timeEntryService.getEmployeeEntries(employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("employeeId", employeeId);
            response.put("count", entries.size());
            response.put("entries", entries);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
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
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);

            List<TimeEntry> entries = timeEntryService.getEmployeeEntriesByDateRange(employeeId, start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("employeeId", employeeId);
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("count", entries.size());
            response.put("entries", entries);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
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
        try {
            TimeEntry activeSession = timeEntryService.getActiveSession(employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("employeeId", employeeId);
            response.put("isClockedIn", activeSession != null);

            if (activeSession != null) {
                response.put("activeEntryId", activeSession.getId());
                response.put("clockInTime", activeSession.getClockInTime());
            } else {
                response.put("message", "No active clock-in session found");
            }

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
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
        try {
            boolean isClockedIn = timeEntryService.isClockedIn(employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("employeeId", employeeId);
            response.put("isClockedIn", isClockedIn);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
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
        try {
            List<TimeEntry> entries = timeEntryService.getTodayEntries(employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("employeeId", employeeId);
            response.put("count", entries.size());
            response.put("entries", entries);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Get All Pending Time Entries (Manager View)
     * GET /api/time/pending
     * 
     * @return List of all pending time entries
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getAllPendingEntries() {
        try {
            List<TimeEntry> pendingEntries = timeEntryService.getAllPendingEntries();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", pendingEntries.size());
            response.put("pendingEntries", pendingEntries);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
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
        try {
            List<TimeEntry> pendingEntries = timeEntryService.getEmployeePendingEntries(employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("employeeId", employeeId);
            response.put("count", pendingEntries.size());
            response.put("pendingEntries", pendingEntries);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
