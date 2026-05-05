package employee.tracker.model;

/**
 * Role Enum for Employee Tracker System
 * Controls access levels and permissions throughout the application
 */
public enum Role {
    
    // Role Definitions

    /**
     * EMPLOYEE - Basic user with standard permissions
     * Can:
     * - Clock in/out
     * - View own schedule
     * - Request PTO
     * - View own time entries
     */
    EMPLOYEE,

    /**
     * MANAGER - Supervisory role with approval powers
     * Can:
     * - Approve timesheets for their team
     * - Edit time entries for their team
     * - Approve/deny PTO requests
     * - View team schedules
     * - Generate team reports
     */
    MANAGER,

    /**
     * ADMIN - Full system access
     * Can:
     * - Configure system rules
     * - Manage all users
     * - Access all data
     * - Configure department setting
     * - System-wide reporting
     */
    ADMIN;

    // Helper Methods

    /**
     * Check if this role has management privileges
     * Useful for authorization logic
     */
    public boolean isManagement() {
        return this == MANAGER || this == ADMIN;
    }

    /**
     * Check if this role has administrative privileges
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Get display-friendly role name
     */
    public String getDisplayName() {
        switch (this) {
            case EMPLOYEE:
                return "Employee";
            case MANAGER:
                return "Manager";
            case ADMIN:
                return "Admin";
        
            default:
                return this.name();
        }
    }
}
