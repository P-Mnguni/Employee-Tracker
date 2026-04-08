package employee.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "time_entries")
public class TimeEntry {
    
    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationship to Employee (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "timesheet_id")
    private Timesheet timesheet;

    // Core Time Tracking Fields
    @Column(name = "clock_in_time", nullable = false)
    private LocalDateTime clockInTime;

    @Column(name = "clock_out_time")
    private LocalDateTime clockOutTime;

    // Status Field
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeEntryStatus status;                     // PENDING, APPROVED, REJECTED

    // Break Handling (Simple approach)
    @Column(name = "break_duration_minutes")
    private Integer breakDurationMinutes = 0;

    // Location Fields
    @Column(name = "gps_location")
    private String gpsLocation;                         // Simple string format: "Latitude,longitude"

    // Separate latitude/longitude fields
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // Timestamps (Future-proofing)
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Additional Notes Field
    @Column(length = 500)
    private String notes;                               // For manager comments or employee notes

    // Constructors
    public TimeEntry() {}

    public TimeEntry(Employee employee, LocalDateTime clockInTime) {
        this.employee = employee;
        this.clockInTime = clockInTime;
        this.status = TimeEntryStatus.PENDING;
        this.breakDurationMinutes = 0;
    }

    public TimeEntry(Employee employee, LocalDateTime clockInTime, String gpsLocation) {
        this(employee, clockInTime);
        this.gpsLocation = gpsLocation;
    }

    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (breakDurationMinutes == null) {
            breakDurationMinutes = 0;
        }
        if (status == null) {
            status = TimeEntryStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper Methods
    
    /**
     * Check if this entry is still open (no clock-out time)
     */
    public boolean isOpen() {
        return clockOutTime == null;
    }

    /**
     * Clock out the employee - sets the clock-out time
     */
    public void clockOut() {
        this.clockOutTime = LocalDateTime.now();
    }

    /**
     * Clock out with specific time (for corrections)
     */
    public void clockOut(LocalDateTime clockOutTime) {
        if (clockOutTime.isAfter(this.clockOutTime)) {
            this.clockOutTime = clockOutTime;
        } else {
            throw new IllegalArgumentException("Clock-out time must be after clock-in time");
        }
    } 

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDateTime getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(LocalDateTime clockInTime) {
        this.clockInTime = clockInTime;
    }

    public LocalDateTime getClockOutTime() {
        return clockOutTime;
    }

    public void setClockOutTime(LocalDateTime clockOutTime) {
        this.clockOutTime = clockOutTime;
    }

    public TimeEntryStatus getStatus() {
        return status;
    }

    public void setStatus(TimeEntryStatus status) {
        this.status = status;
    }

    public Integer getBreakDurationMinutes() {
        return breakDurationMinutes;
    }

    public void setBreakDurationMinutes(Integer breakDurationMinutes) {
        this.breakDurationMinutes = breakDurationMinutes;
    }

    public String getGpsLocation() {
        return gpsLocation;
    }

    public void setGpsLocation(String gpsLocation) {
        this.gpsLocation = gpsLocation;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // toString() for debugging
    @Override
    public String toString() {
        return "TimeEntry{" + 
                "id=" + 
                ", employee=" + (employee != null ? employee.getName() : null) + 
                ", clockInTime=" + clockInTime + 
                ", clockOutTime=" + clockOutTime + 
                ", status=" + status + 
                ", breakDurationMinutes=" + breakDurationMinutes + 
                ", gpsLocation='" + gpsLocation + '\'' + 
                '}';
    }
}
