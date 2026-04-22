# Employee Tracker - Model Layer

This package contains all the entity and enum classes that form
the data model for the Employee Tracker System.

## 📊 Model Overview

### Entity Classes (8)

```
|   Entity     |           Purpose              |                Key Fields                   |
|--------------|--------------------------------|---------------------------------------------|
| Employee     | Core user entity               | name, email, department, role               |
| TimeEntry    | Individual clock-in/out record | clockInTime, clockOutTime, status           |
| Timesheet    | Weekly/bi-weekly work summary  | startDate, endDate, status                  |
| PTORequest   | Leave/PTO requests             | startDate, endDate, leaveType, status       |
| Shift        | Scheduled work hours           | shiftDate, startTime, endTime, shiftType    |
| BreakPeriod  | Break time within shifts       | startTime, endTime, breakType               |
| OvertimeRule | Overtime configuration         | dailyThreshold, weeklyThreshold, multiplier |
```

### Enum Classes (7)

```
|      Enum       |                Values              |   Used In   |
|-----------------|------------------------------------|-------------|
| Role            | EMPLOYEE, MANAGER, ADMIN           | Employee    |
| TimeEntryStatus | PENDING, APPROVED, REJECTED        | TimeEntry   |
| TimesheetStatus | DRAFT, PENDING, APPROVED, REJECTED | Timesheet   |
| PTOStatus       | PENDING, APPROVED, REJECTED        | PTORequest  |
| LeaveType       | PTO, SICK, UNPAID                  | PTORequest  |
| ShiftType       | MORNING, EVENING, NIGHT            | Shift       |
| BreakType       | LUNCH, SHORT_BREAK, PAID_BREAK     | BreakPeriod |
```

## 🔗 Entity Relationships
```
Employee (1) ----------< (Many) TimeEntry
|
|----------< (Many) Timesheet
|
|----------< (Many) PTORequest
|
|----------< (Many) Shift (1) ----------< (Many) BreakPeriod

OvertimeRule (no direct relationships - configuration only)
```

## 📋 Status Workflows

### Timesheet Workflow

DRAFT → PENDING → APPROVED
    ↘ REJECTED → DRAFT (for editing)

### PTO Request Workflow

PENDING → APPROVED
       ↘ REJECTED

### Time Entry Workflow

PENDING → APPROVED
       ↘ REJECTED

## 🗄️ Database Tables

Each entity maps to a database table:
- `employees`
- `time_entries`
- `timesheets`
- `pto_requests`
- `shifts`
- `break_periods`
- `overtime_rules`

## 🎯 Design Principles

- **No business logic** - Pure data classes with JPA annotations
- **Bidirectional relationships** - Proper `@OneToMany` / `@ManyToOne` mappings
- **Timestamp tracking** - `createdAt` and `updatedAt` on all entities
- **Enum for fixed values** - Type safety for roles, statuses, and types
- **Future-proof** - Optional fields included for planned features

## ✅ Validation

All model classes compile without errors:
```bash
./mvnw clean compile
```

## Next Steps

The following layers will be built next:
- **Repository Layer** - Database operations for each entity
- **Service Layer** - Business logic and workflows
- **Controller Layer** - REST API endpoints
- **DTO Layer** - Data transfer objects for API responses