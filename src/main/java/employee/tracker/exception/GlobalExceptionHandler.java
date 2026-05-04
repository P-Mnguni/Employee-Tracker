package employee.tracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler - Centralized exception handling for all controllers
 * 
 * This class intercepts exceptions thrown anywhere in the application and
 * returns consistent, structured error responses to the client.
 * 
 * Benefits:
 * - Standardized error format across all APIs
 * - Clean controller code (no try-catch blocks)
 * - Professional error messages
 * - Proper HTTP status codes
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Error response structure - consistent across  all endpoints
     */
    private Map<String, Object> buildErrorResponse(HttpStatus status, String message, WebRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", getPath(request));
        errorResponse.put("success", false);
        
        return errorResponse;
    }

    /**
     * Extract the request path for error response
     */
    private String getPath(WebRequest request) {
        String path = request.getDescription(false);
        if (path != null && path.startsWith("url=")) {
            path = path.substring(4);
        }
        return path;
    }

    // === Resource Not Found Handler (404) ===

    /**
     * Handles cases where a requested resource doesn't exist
     * Example: Employee not found, Timesheet not found, PTO not found
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String message = ex.getMessage();

        // Check if this is a "not found" type error
        if (message != null && (message.contains("not found") || message.contains("does not exist"))) {
            Map<String, Object> errorResponse = buildErrorResponse(HttpStatus.NOT_FOUND, message, request);

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        // Check if this is a business rule violation
        if (message != null && (message.contains("cannot") || message.contains("must") || message.contains("invalid"))) {
            Map<String, Object> errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Default handling for other runtime exceptions
        Map<String, Object> errorResponse = buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + message, request
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // === Business Rule Violation Handler (400) ===

    /**
     * Handles IllegalStateException - Business rule violation
     * Examples:
     * - Employee already clocked in
     * - Cannot approve already approved timesheet
     * - Only PENDING requests can be approved
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        Map<String, Object> errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles IllegalArgumentException - Invalid input values
     * Examples:
     * - Invalid date range (start after end)
     * - Negative values
     * - Empty required fields
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // === Validation Errors (400) ===

    /**
     * Handles method argument type mismatches
     * Example: Sending text where a number is expected
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String message = String.format(
            "Invalid value '%s' for parameter '%s'. Expected type: %s", 
            ex.getValue(),
            ex.getName(),
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );

        Map<String, Object> errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles malformed JSON requests
     * Example: Missing quotes, invalid date format
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        String message = "Malformed JSON request. Please check your request body format.";

        // Provide more specific message for common issues
        if (ex.getMessage() != null && ex.getMessage().contains("LocalDate")) {
            message = "Invalid date format. Please use ISO format: yyyy-MM-dd (e.g., 2026-05-03)";
        } else if (ex.getMessage() != null && ex.getMessage().contains("LocalDateTime")) {
            message = "Invalid date-time format. Please use ISO format: yyyy-MM-ddTHH:mm:ss (e.g., 2026-05-03T10:30:00)";
        }

        Map<String, Object> errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // === General Exception Handler (500) ===

    /**
     * Handles any uncaught exceptions (fallback)
     * This ensures the client always receives a structured response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex, WebRequest request) {
        // Log the full exception for debugging
        System.err.println("Unhandled exception: " + ex.getMessage());
        ex.printStackTrace();

        Map<String, Object> errorResponse = buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred. Please try again later", request);

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
