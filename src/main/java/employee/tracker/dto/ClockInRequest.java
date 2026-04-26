package employee.tracker.dto;

/**
 * ClockInRequest DTO - Data transfer object for clock-in API requests
 * 
 * This represents the data sent from the client when an employee clocks in.
 * Using DTOs separates the API contract from the database entities,
 * allowing for cleaner validation and future flexibility.
 * 
 * Example JSON request:
 * {
 *      "employeeId": 1,
 *      "gpsLocation": "37.7749,-122.4194"
 * }
 */

public class ClockInRequest {
    
    // === Core & Future-ready Fields ===

    /**
     * The ID of the employee clocking in
     * This is required and must correspond to an existing employee
     */
    private Long employeeId;

    /**
     * GPS location of the clock-in
     * Format: "latitude,longitude" (e.g., "37.7749,-122.4194")
     * Used for: Location tracking, fraud prevention, attendance verification
     */
    private String gpsLocation;

    /**
     * IP address of the client making the request
     * Useful for audit trails and security monitoring
     */
    private String ipAddress;

    /**
     * Device information (browser, OS, device type)
     * Useful for tracking and analytics
     */
    private String deviceInfo;

    // === Constructor ===

    public ClockInRequest() {}

    public ClockInRequest(Long employeeId) {
        this.employeeId = employeeId;
    }

    public ClockInRequest(Long employeeId, String gpsLocation) {
        this.employeeId = employeeId;
        this.gpsLocation = gpsLocation;
    }

    public ClockInRequest(Long employeeId, String gpsLocation, String ipAddress, String deviceInfo) {
        this.employeeId = employeeId;
        this.gpsLocation = gpsLocation;
        this.ipAddress = ipAddress;
        this.deviceInfo = deviceInfo;
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
        if (gpsLocation == null) {
            return true;
        }
        return getLatitude() != null && getLongitude() != null;
    }

    // === toString() for debugging ===

    @Override
    public String toString() {
        return "ClockInRequest{" +
                "employeeId=" + employeeId +
                ", gpsLocation=" + gpsLocation + '\'' +
                ", ipAddress=" + ipAddress + '\'' +
                ", deviceInfo=" + deviceInfo + '\'' +
                '}';
    }
}
