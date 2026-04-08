package employee.tracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
public class Employee {
    
    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic Info
    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    // Organization
    @Column(nullable = false)
    private String department;

    // Role
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;                  // ADMIN, MANAGER, EMPLOYEE

    // Schedule Relationship (One-to-Many with Shift)
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Shift> shifts = new ArrayList<>();

    // Future-Proofing: Timestamps
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Additional Relationships (future-ready, commented for now)
    // @OneToMany(mappedBy = "employee")
    // private List<TimeEntry> timeEntries = new ArrayList<>();

    // @OneToMany(mappedBy = "employee")
    // private List<PTORequest> ptoRequests = new ArrayList<>();

    // Constructors
    public Employee() {}

    public Employee(String name, String email, String department, Role role) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.role = role;
    }

    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    public List<Shift> getShifts() {
        return shifts;
    }

    public void setShifts(List<Shifts> shifts) {
        this.shifts = shifts;
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

    // Helper Methods
    public void addShift(Shift shift) {
        shifts.add(shift);
        shift.setEmployee(this);
    }

    public void removeShift(Shift shift) {
        shifts.remove(shift);
        shift.setEmployee(null);
    }

    // toString() for debugging
    @Override
    public String toString() {
        return "Employee{" + 
                "id=" + id + 
                ", name=" + name + '\'' + 
                ", email=" + email + '\'' + 
                ", department=" + department + '\'' + 
                ", role=" + role + 
                ", createdAt=" + createdAt + 
                ", updatedAt" + updatedAt + 
                "}"; 
    }
}