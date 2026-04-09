package employee.tracker.model;

/**
 * TimesheetStatus Enum for Employee Tracker System
 * Defines the lifecycle of a timesheet from submission to approval
 */
public enum TimesheetStatus {
    
    // Status Definitions

    /**
     * DRAFT - Timesheet is being created/edited but not yet submitted
     * 
     * Characteristics:
     * - Employee is actively working on timesheet
     * - Can be freely edited
     * - Not yet visible to managers
     * - Employee can submit when ready
     */
    DRAFT,

    /**
     * PENDING - Timesheet has been submitted and is waiting for manager review
     * 
     * Characteristics:
     * - Employee has submitted the timesheet
     * - Waiting for manager action (approve or reject)
     * - Cannot be edited by employee
     * - Manager can approve or reject
     */
    PENDING,

    /**
     * APPROVED - Timesheet has been approved by manager
     * 
     * Characteristics:
     * - Manager has approved the timesheet
     * - Locked for any further edits
     * - Ready for payroll processing
     * - Cannot be changed by anyone (except admin override)
     */
    APPROVED,

    /**
     * REJECTED - Timesheet was rejected by manager
     * 
     * Characteristics:
     * - Manager has rejected the timesheet
     * - Employee must fix time entries
     * - Employee can resubmit after corrections
     * - Rejection reason should be provided by manager
     */
    REJECTED;

    // Helper Methods

    /**
     * Check if timesheet can be editable
     * Only REJECTED timesheets can be edited
     */
    public boolean isEditable() {
        return this == REJECTED;
    }

    /**
     * Check if timesheet can be submitted
     * DRAFT and REJECTED timesheets can be submitted
     */
    public boolean isSubmittable() {
        return this == DRAFT || this == REJECTED;
    }

    /**
     * Check if timesheet can be approved
     * Only PENDING timesheets can be approved
     */
    public boolean isApprovable() {
        return this == PENDING;
    }

    /**
     * Check if timesheet can be rejected
     * Only PENDING timesheets can be rejected
     */
    public boolean isRejectable() {
        return this == PENDING;
    }

    /**
     * Check if timesheet is in a final state (no more changes possible)
     */
    public boolean isFinal() {
        return this == APPROVED;
    }

    /**
     * Check if timesheet is currently under review
     */
    public boolean isUnderReview() {
        return this == PENDING;
    }

    /**
     * Get user-friendly display name
     */
    public String getDisplayName() {
        switch(this) {
            case DRAFT:
                return "Draft";
            case PENDING:
                return "Pending Review";
            case APPROVED:
                return "Approved";
            case REJECTED:
                return "Rejected - Needs Changes";
            default:
                return this.name();
        }
    }
}
