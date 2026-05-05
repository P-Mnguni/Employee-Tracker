package employee.tracker.dto;

/**
 * ClockOutRequest DTO - Data transfer object for clock-out API requests
 * 
 * This represents the data sent from the client when an employee clock out.
 * Separating from ClockInRequest allows different validations and future
 * enhancements specific to ending a work session.
 * 
 * Example JSON request:
 * {
 *      "employeeId": 1,
 *      "gpsLocation": "37.7749,-122.4194"
 * }
 */
public class ClockOutRequest {
    
    /**
     * The ID of the employee clocking out
     * This is required and must correspond to an existing employee
     * who is currently clocked in
     */
    private Long employeeId;

    /**
     * GPS location of the clock-out
     * Format: "latitude,longitude" (e.g., "37.7749-122.4194")
     * Used for: Location validation against clock-in location,
     * fraud prevention, attendance verification
     */
    private String gpsLocation;

    /**
     * IP address for the client making the request
     * Useful for audit trails and security monitoring
     */
    private String ipAddress;

    /**
     * Device information (browser, OS, device type)
     * Useful for tracking and analytics
     */
    private String deviceInfo;

    /**
     * Any notes the employee wants to add about their work session
     * Example: "Worked on project X, had 1 hour meeting"
     */
    private String notes;

    // === Constructors ===

    public ClockOutRequest() {}

    public ClockOutRequest(Long employeeId) {
        this.employeeId = employeeId;
    }

    public ClockOutRequest(Long employeeId, String gpsLocation) {
        this.employeeId = employeeId;
        this.gpsLocation = gpsLocation;
    }

    public ClockOutRequest(Long employeeId, String gpsLocation, String ipAddress, String deviceInfo, String notes) {
        this.employeeId = employeeId;
        this.gpsLocation = gpsLocation;
        this.ipAddress = ipAddress;
        this.deviceInfo = deviceInfo;
        this.notes = notes;
    }

    // === Getters and Setters ===

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getGpsLocation() {
        return gpsLocation;
    }

    public void setGpsLocation(String gpsLocation) {
        this.gpsLocation = gpsLocation;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // === Helper Methods ===

    /**
     * Extract latitude from GPS location string
     * Format expected: "latitude,longitude"
     */
    public Double getLatitude() {
        if (gpsLocation == null || !gpsLocation.contains(",")) {
            return null;
        }
        try {
            String[] parts = gpsLocation.split(",");
            return Double.parseDouble(parts[0].trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Extract longitude from GPS location string
     * Format expected: "latitude,longitude"
     */
    public Double getLongitude() {
        if (gpsLocation == null || !gpsLocation.contains(",")) {
            return null;
        }
        try {
            String[] parts = gpsLocation.split(",");
            return Double.parseDouble(parts[1].trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Validate that GPS location is in correct format
     */
    public boolean isValidGpsLocation() {
        if (gpsLocation == null) return true;
        return getLatitude() != null && getLongitude() != null;
    }

    /**
     * Validate that request has minimum required fields
     */
    public boolean isValid() {
        return employeeId != null && employeeId > 0;
    }

    // === toString() ===

    @Override
    public String toString() {
        return "ClockOutRequest{" +
                "employeeId=" + employeeId +
                ", gpsLocation=" + gpsLocation + '\'' +
                ", ipAddress=" + ipAddress + '\'' +
                ", deviceInfo=" + deviceInfo + '\'' +
                ", notes=" + notes + '\'' +
                '}';
    }
}
