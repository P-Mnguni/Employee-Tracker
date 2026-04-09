package employee.tracker.model;

/**
 * PTOStatus Enum - Defines the approval lifecycle of a PTO request
 */
public enum PTOStatus {
    
    /**
     * PENDING - Waiting for manager decision
     */
    PENDING,

    /**
     * APPROVED - Leave request has been accepted
     */
    APPROVED,

    /**
     * REJECTED - Leave request has been declined
     */
    REJECTED,

    /**
     * CANCELLED - Leave request has been cancelled
     */
    CANCELLED;
}
