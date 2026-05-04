package employee.tracker.exception;

/**
 * ResourceNotFoundException - Thrown when a requested resource doesn't exist
 * 
 * Examples:
 * - Employee not found with given ID
 * - Timesheet not found
 * - PTO request not found
 * - Shift not found
 * 
 * This exception results in HTTP 404 Not Found response.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Constructs a new ResourceNotFoundException with the specified detail message
     * 
     * @param message The detail message explaining which resource wasn't found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with a formatted message
     * 
     * @param resourceType The type of resource (e.g., "Employee", "Timesheet")
     * @param id The ID that was searched for
     */
    public ResourceNotFoundException(String resourceType, Long id) {
        super(String.format("%s not found with id: %d", resourceType, id));
    }

    /**
     * Constructs a new ResourceNotFoundException with a formatted message and cause
     * 
     * @param resourceType The type of resource
     * @param id The ID that was searched for 
     * @param cause The underlying cause
     */
    public ResourceNotFoundException(String resourceType, Long id, Throwable cause) {
        super(String.format("%s not found with id: %d", resourceType, id), cause);
    }

    /**
     * Constructs a new ResourceNotFoundException with a custom formatted message
     * 
     * @param message The format string
     * @param args Arguments referenced by the format specifiers
     */
    public ResourceNotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }
}
