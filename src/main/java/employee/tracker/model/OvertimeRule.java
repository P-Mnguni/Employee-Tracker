package employee.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "overtime_rules")
public class OvertimeRule {
    
    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Thresholds
    @Column(name = "daily_threshold_hours", nullable = false)
    private Double dailyThresholdHours;                          // Hours before daily overtime applies (e.g., 8.0)

    @Column(name = "weekly_threshold_hours", nullable = false)
    private Double weeklyThresholdHours;                        // e.g., 40.0

    // Multiplier
    @Column(name = "multiplier", nullable = false)
    private Double multiplier;                                  // Overtime pay rate (e.g., 1.5 for time-and-a-half)

    // Active Flag
    @Column(name = "is_active")
    private Boolean isActive = true;                            // Indicates if rule is currently in use

    // Effective Dates
    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom;                        // When this rule starts applying

    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;                          // When this rule expires

    // Rule Name/Description
    @Column(name = "rule_name")
    private String ruleName;                                    // e.g., "Standard Overtime Rule"

    @Column(length = 500)
    private String description;                                 // e.g., "Time-and-a-half after 8 hours daily or 40 hours weekly"

    // Advanced Overtime Types
    @Column(name = "double_time_multiplier")
    private Double doubleTimeMultiplier;                        // For extreme overtime (e.g., 2.0)

    @Column(name = "double_time_threshold_hours")
    private Double doubleTimeThresholdHours;                    // Hours before double time applies

    // Timestamps
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    // Constructors
    public OvertimeRule() {}

    public OvertimeRule(Double dailyThresholdHours, Double weeklyThresholdHours, Double multiplier) {
        this.dailyThresholdHours = dailyThresholdHours;
        this.weeklyThresholdHours = weeklyThresholdHours;
        this.multiplier = multiplier;
        this.isActive = true;
    }

    public OvertimeRule(Double dailyThresholdHours, Double weeklyThresholdHours, Double multiplier, String ruleName) {
        this(dailyThresholdHours, weeklyThresholdHours, multiplier);
        this.ruleName = ruleName;
    }

    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (isActive == null) {
            isActive = true;
        }
        if (dailyThresholdHours == null) {
            dailyThresholdHours = 8.0;
        }
        if (weeklyThresholdHours == null) {
            weeklyThresholdHours = 40.0;
        }
        if (multiplier == null) {
            multiplier = 1.5;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper Methods

    /**
     * Check if this rule is currently active
     */
    public boolean isCurrentlyActive() {
        if (!isActive) return false;

        LocalDateTime now = LocalDateTime.now();

        if (effectiveFrom != null && now.isBefore(effectiveFrom)) {
            return false;
        }

        if (effectiveTo != null && now.isAfter(effectiveTo)) {
            return false;
        }

        return true;
    }

    /**
     * Check if double time is configured
     */
    public boolean hasDoubleTime() {
        return doubleTimeMultiplier != null && doubleTimeThresholdHours != null;
    }

    /**
     * Get the multiplier to use for a given number of hours
     * Note: Actual calculation logic belongs in service layer
     */
    public Double getApplicableMultiplier(Double hoursWorked) {
        if (hasDoubleTime() && hoursWorked > doubleTimeThresholdHours) {
            return doubleTimeMultiplier;
        }
        return multiplier;
    }

    /**
     * Get display-friendly rule summary
     */
    public String getRuleSummary() {
        StringBuilder sb = new StringBuilder();

        if (ruleName != null) {
            sb.append(ruleName).append(": ");
        }

        sb.append(String.format("Daily > %.1f hrs, Weekly > %.1f hrs @ %.1fx",
                                dailyThresholdHours, weeklyThresholdHours, multiplier));

        if (hasDoubleTime()) {
            sb.append(String.format("(Double time > %.1f hrs @ %.1fx)",
                                    doubleTimeThresholdHours, doubleTimeMultiplier));
        }

        return sb.toString();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getDailyThresholdHours() {
        return dailyThresholdHours;
    }

    public void setDailyThresholdHours(Double dailyThresholdHours) {
        this.dailyThresholdHours = dailyThresholdHours;
    }

    public Double getWeeklyThresholdHours() {
        return weeklyThresholdHours;
    }

    public void setWeeklyThresholdHours(Double weeklyThresholdHours) {
        this.weeklyThresholdHours = weeklyThresholdHours;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDateTime effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDateTime getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDateTime effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getDoubleTimeMultiplier() {
        return doubleTimeMultiplier;
    }

    public void setDoubleTimeMultiplier(Double doubleTimeMultiplier) {
        this.doubleTimeMultiplier = doubleTimeMultiplier;
    }

    public Double getDoubleTimeThresholdHours() {
        return doubleTimeThresholdHours;
    }

    public void setDoubleTimeThresholdHours(Double doubleTimeThresholdHours) {
        this.doubleTimeThresholdHours = doubleTimeThresholdHours;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    // toString() for debugging
    @Override
    public String toString() {
        return "OvertimeRule{" +
                "id=" + id +
                ", dailyThresholdHours=" + dailyThresholdHours +
                ", weeklyThresholdHours=" + weeklyThresholdHours +
                ", multiplier=" + multiplier +
                ", isActive=" + isActive +
                ", ruleName=" + ruleName +
                ", effectiveFrom=" + effectiveFrom +
                ", effectiveTo=" + effectiveTo +
                '}';
    }
}
