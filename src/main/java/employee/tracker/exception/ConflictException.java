package employee.tracker.exception;

/**
 * ConflictException - Thrown when a request conflicts with existing data
 * 
 * Examples:
 * - Double clock-in (already has active session)
 * - Overlapping PTO requests
 * - Duplicate timesheet for period
 * - Email already exists for another employee
 * 
 * This exception results in HTTP 409 conflict response.
 */
public class ConflictException extends RuntimeException {
    
    /**
     * Constructs a new ConflictException with the specified detail message
     * 
     * @param message The detail message explaining the conflict
     */
    public ConflictException(String message) {
        super(message);
    }

    /**
     * Constructs a new ConflictException with a formatted message
     * 
     * @param conflictType The type of conflict (e.g., "active session", "overlapping PTO")
     * @param userId The ID of the user involved
     */
    public ConflictException(String conflictType, Long userId) {
        super(String.format("Conflict detected: %s for user %d", conflictType, userId));
    }

    /**
     * Constructs a new ConflictException with a custom formatted message
     * 
     * @param message The format string
     * @param args Arguments referenced by the format specifiers
     */
    public ConflictException(String message, Object... args) {
        super(String.format(message, args));
    }
}
