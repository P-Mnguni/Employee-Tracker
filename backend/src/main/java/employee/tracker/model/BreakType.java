package employee.tracker.model;

/**
 * BreakType Enum - Defines the type of break taken during a shift
 */
public enum BreakType {
    
    /**
     * LUNCH - Meal break (typically 30-60 minutes)
     */
    LUNCH,

    /**
     * SHORT_BREAK - Quick rest break (typically 10-15 minutes)
     */
    SHORT_BREAK,

    /**
     * PAID_BREAK - Break that is paid (not deducted from hours)
     */
    PAID_BREAK;
}
