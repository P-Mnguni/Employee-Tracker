package employee.tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import employee.tracker.model.Employee;
import org.springframework.stereotype.Repository;


/**
 * EmployeeRepository - Data access layer for Employee entities
 * 
 * This interface provides automatic database operations for Employee objects
 * Spring Data JPA generates the implementation at runtime
 * 
 * Built-in methods provided by JpaRepository:
 * - save(Employee employee) - Save or update an employee
 * - findById(Long id) - Find employee by ID
 * - findAll() - Get all employees
 * - delete(Employee employee) - Delete an employee
 * - deleteById(Long id) - Delete employee by ID
 * - count() - Count total employees
 * - existsById(Long id) - Check if employee exists
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    // Custom queries will go here in future
    // Spring will automatically implement methods following naming conventions:

    // Example: Find employees by email (to be added later)
    // Employee findByEmail(String email);

    // Example: Find employees by department (to be added later)
    // List<Employee> findByDepartment(String department);

    // Example: Find employees by role (to be added later)
    // List<Employee> findByRole(Role role); 

    // Example: Find employees by name containing (search feature)
    // List<Employee> findByNameContainingIgnoreCase(String name); 
}
