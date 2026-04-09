package employee.tracker.model;

/**
 * TimeEntryStatus Enum - Defines the state of a TimeEntry during its lifecycle
 */
public enum TimeEntryStatus {
    
    /**
     * PENDING - Entry has been created (clock-in/out done)
     * Not yet reviewed by manager
     */
    PENDING,

    /**
     * APPROVED - Manager has validated the entry
     * Can be used in payroll calculations
     */
    APPROVED,

    /**
     * REJECTED - Entry has issues (e.g., incorrect time)
     * Needs corrections
     */
    REJECTED;
}
