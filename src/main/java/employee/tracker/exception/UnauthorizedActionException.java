package employee.tracker.exception;

/**
 * UnauthorizedActionException - Thrown when a user attempts an action they don't have permission for
 * 
 * Examples:
 * - Employee trying to approve their own timesheet
 * - Manager trying to approve timesheet for another department
 * - Employee trying to view another employee's data
 * - Non-admin trying to configure overtime rules
 * 
 * This exception results in HTTP 403 Forbidden response.
 */
public class UnauthorizedActionException extends RuntimeException {
    
    /**
     * Constructs a new UnauthorizedActionException with the specified detail message
     * 
     * @param message The detail message explaining why the action is unauthorized
     */
    public UnauthorizedActionException(String message) {
        super(message);
    }

    /**
     * Constructs a new UnauthorizedActionException with a formatted message
     * 
     * @param action The action that was attempted
     * @param userId The ID of the user who attempted the action
     * @param requiredRole The role required for this action
     */
    public UnauthorizedActionException(String action, Long userId, String requiredRole) {
        super(String.format("User %d is not authorized to perform '%s'. Required role: %s", userId, action, requiredRole));
    }

    /**
     * Constructs a new UnauthorizedActionException with a formatted message and cause
     * 
     * @param action The action that was attempted
     * @param userId The ID of the user who attempted the action
     * @param reason The specific reason for denial
     */
    public UnauthorizedActionException(String action, Long userId, String reason, boolean isDetailed) {
        super(String.format("User %d cannot perform '%s': %s", userId, action, reason));
    }

    /**
     * Constructs a new UnauthorizedActionException with cause
     * 
     * @param message The detail message
     * @param cause The underlying cause
     */
    public UnauthorizedActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
