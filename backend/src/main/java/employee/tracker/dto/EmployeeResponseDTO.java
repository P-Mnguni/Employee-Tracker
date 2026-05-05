package employee.tracker.dto;

import employee.tracker.model.Role;
import java.time.LocalDateTime;

/**
 * EmployeeResponseDTO - Data transfer object for Employee API responses
 * 
 * Contains only the fields needed for client responses, avoiding circular
 * references to TimeEntry, Timesheet, and PTORequest.
 */
public class EmployeeResponseDTO {
    
    private Long id;
    private String name;
    private String email;
    private String department;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Statistics (for dashboard views)
    private Integer totalTimeEntries;
    private Integer totalTimesheets;
    private Integer totalPTORequests;
    private Double totalPTODaysTaken;

    public EmployeeResponseDTO() {}

    public EmployeeResponseDTO(Long id, String name, String email, String department, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public Integer getTotalTimeEntries() {
        return totalTimeEntries;
    }

    public void setTotalTimeEntries(Integer totalTimeEntries) {
        this.totalTimeEntries = totalTimeEntries;
    }

    public Integer getTotalTimesheets() {
        return totalTimesheets;
    }

    public void setTotalTimesheets(Integer totalTimesheets) {
        this.totalTimesheets = totalTimesheets;
    }

    public Integer getTotalPTORequests() {
        return totalPTORequests;
    }

    public void setTotalPTORequests(Integer totalPTORequests) {
        this.totalPTORequests = totalPTORequests;
    }

    public Double getTotalPTODaysTaken() {
        return totalPTODaysTaken;
    }

    public void setTotalPTODaysTaken(Double totalPTODaysTaken) {
        this.totalPTODaysTaken = totalPTODaysTaken;
    }
}
