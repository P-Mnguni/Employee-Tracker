package employee.tracker.exception;

/**
 * BadRequestException - Thrown when a request contains invalid data or violates business rules
 * 
 * Examples:
 * - Clock-out without clocking in first
 * - Invalid date range (start date after end date)
 * - Requesting past leave dates
 * - Approving already approved timesheet
 * - Submitting empty timesheet
 * - Duplicate timesheet for period
 * 
 * This exception results in HTTP 400 Bad Request response.
 */
public class BadRequestException extends RuntimeException {
    
    /**
     * Constructs a new BadRequestException with the specified detail message
     * 
     * @param message The detail message explaining what was invalid
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Constructs a new BadRequestException with a formatted message
     * 
     * @param operation The operation that was attempted
     * @param reason The reason it failed
     */
    public BadRequestException(String operation, String reason) {
        super(String.format("Cannot perform '%s': %s", operation, reason));
    }

    /**
     * Constructs a new BadRequestException with cause
     * 
     * @param message The detail message
     * @param cause The underlying cause
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new BadRequestException with a custom formatted message
     * 
     * @param message The format string
     * @param args Arguments referenced by the format specifiers
     */
    public BadRequestException(String message, Object... args) {
        super(String.format(message, args));
    }
}
