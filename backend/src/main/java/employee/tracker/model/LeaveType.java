package employee.tracker.model;

/**
 * LeaveType Enum - Defines types of leave employees can request
 */
public enum LeaveType {
    
    /**
     * PTO - Paid Time Off (vacation, personal days)
     */
    PTO,

    /**
     * SICK - Sick leave (medical appointments, illness)
     */
    SICK,

    /**
     * UNPAID - Unpaid leave (no salary for these days)
     */
    UNPAID;
}
