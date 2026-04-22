# Employee Tracker - Repository Layer

This package contains all the repository interfaces that handle database
operations for the Employee Tracker System.

## 📊 Repository Overview

### Repository Interfaces (4)

```
|     Repository       |   Entity   |         Purpose          |                              Key Custom Queries                              |
|----------------------|------------|--------------------------|------------------------------------------------------------------------------|
| EmployeeRepository   | Employee   | Basic CRUD operations    | None yet (extends JpaRepository)                                             |
| TimeEntryRepository  | TimeEntry  | Clock-in/out records     | findByEmployee, date ranges, open entries, status filtering                  |
| TimesheetRepository  | Timesheet  | Timesheet management     | findByStatus, date ranges, department filtering, approval workflow           |
| PTORequestRepository | PTORequest | Leave request management | findByStatus, overlapping requests, leave type filtering, conflict detection |
```

## 🗄️ Repository Details

### 1. EmployeeRepository
**Base interface:** `JpaRepository<Employee, Long>`

**Built-in methods available:**
- `save()` - Save or update employee
- `findById()` - Find employee by ID
- `findAll()` - Get all employees
- `delete()` - Delete employee
- `count()` - Count total employees

**Future custom queries**
- `findByEmail(String email)`
- `findByDepartment(String department)`
- `findByRole(Role role)`

### 2. TimeEntryRepository
**Base interface:** `JpaRepository<TimeEntry, Long>`

**Custom queries:**

```
|                 Method                    |              Purpose              |
|-------------------------------------------|-----------------------------------|
| findByEmployee()                          | Get all entries for an employee   |
| findByEmployeeAndClockInTimeBetween()     | Get entries in data range         |
| findByEmployeeAndClockOutTimeIsNull()     | Find active sessions              |
| findByStatus()                            | Get entries by approval status    |
| getTotalMinutesWorked()                   | Calculate total hours for payroll |
| existsByEmployeeIdAndClockOutTimeIsNull() | Check if employee is clocked in   |
```

### 3. TimesheetRepository
**Base interface:** `JpaRepository<Timesheet, Long>`

**Custom queries:**

```
|                  Method                    |                   Purpose                     |
|--------------------------------------------|-----------------------------------------------|
| findByEmployee()                           | Get all timesheets for an employee            |
| findByStatus()                             | Get timesheets by status                      |
| findByEmployeeAndStartDateBetween()        | Get timesheets in date range                  |
| findByEmployeeDepartmentAndStatus()        | Department manager view of pending timesheets |
| findByStatusAndStartDateBetween()          | Payroll processing queries                    |
| existsByEmployeeIdAndStartDateAndEndDate() | Prevent duplicate timesheets                  |
```

### 4. PTORequestRepository
**Base interface:** `JpaRepository<PTORequest, Long>`

**Custom queries:**

```
|          Method           |                 Purpose                |
|---------------------------|----------------------------------------|
| findByEmployee()          | Get all leave requests for an employee |
| findByStatus()            | Get requests by status                 |
| findByStartDateBetween()  | Get requests in date range             |
| findOverlappingRequests() | Check for scheduling conflicts         |
| findByLeaveType()         | Filter by leave type                   |
| getTotalDaysTaken()       | Calculate total leave used             |
| hasConflictingRequests()  | Prevent double-booking leave           |
```

## 🔄️ Repository Relationships

EmployeeRepository      - Used by all other repositories (through Employee entity)
TimeEntryRepository     - Uses EmployeeRepository for employee lookups
TimesheetRepository     - Uses EmployeeRepository for employee lookups
                        - Related to TimeEntry (Through Timesheet entity)
PTORequestRepository    - Uses EmployeeRepository for employee lookups

## 🎯 Common Query Patterns

### Find by Employee
```java
List<TimeEntry> findByEmployee(Employee employee);
List<Timesheet> findByEmployeeId(Long employeeId);
```

### Find by Status
```java
List<Timesheet> findByStatus(TimesheetStatus status);
List<PTORequest> findByStatus(PTOStatus status);
```

### Find by Date Range
```java
List<TimeEntry> findByEmployeeAndClockInTimeBetween(Employee emp, LocalDateTime start, LocalDateTime end);
List<Timesheet> findByEmployeeAndStartDateBetween(Employee emp, LocalDate start, LocalDate end);
```

### Find Open/Active Records
```java
List<TimeEntry> findByEmployeeAndClockOutTimeIsNull(Employee employee);     // Active clock-in
```

### Conflict Detection
```java
@Query("SELECT COUNT(p) > 0 FROM PTORequest p WHERE p.employee.id = :employeeId " +
    "AND p.status = 'APPROVED' AND p.startDate <= :endDate AND p.endDate >= :startDate")
boolean hasConflictingRequest(...);
```

## ✅ Design Principles

- No business logic - Repositories only handle database operations
- Consistent naming - Follows Spring Data JPA naming conventions
- Type safety - Uses enums instead of strings for status fields
- Query optimization - Uses @Query for complex JPQL queries
- Proper documentation - Each method includes JavaDoc

## Future Repositories (To Be Added)

- ShiftRepository - For scheduled shift operations
- BreakPeriodRepository - For break period management
- OvertimeRuleRepository - For overtime rule configuration

## 🧪 Testing

All repositories are tested through:
- Integration tests with H2 database
- Service layer unit tests with mocked repositories