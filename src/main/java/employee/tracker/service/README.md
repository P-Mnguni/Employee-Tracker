# Employee Tracker - Service Layer

This package contains all the business logic for the Employee Tracker System.
Services act as the bridge between the repository layer (data access) and the 
controller layer (REST APIs).

## 📊 Service Overview

### Service Classes (3)

```
|     Service      |             Purpose              |                     Key Operations                    |
|------------------|----------------------------------|-------------------------------------------------------|
| TimeEntryService | Time tracking & clock management | clockIn, clockOut, session validation                 |
| TimesheetService | PTOService                       | submitTimesheet, approveTimesheet, rejectTimesheet    |
| PTOService       | Leave request management         | requestPTO, approvePTO, rejectPTO, conflict detection | 
```

## 🗄️ Service Details

### 1. TimeEntryService
**Purpose:** Core time tracking logic for employee clock-in/out operations.

```
|             Method                 |       Description        |            Business Rules               |
|------------------------------------|--------------------------|-----------------------------------------|
| clockIn(employeeId)                | Employee starts work     | No double clock-in, employee must exist |
| clockOut(employeeId)               | Employee ends work       | Must be clocked in first                |
| clockOutWithTime(employeeId, time) | Manual clock-out         | For corrections/ back-dating            |
| getEmployeeEntries(employeeId)     | Get all employee entries | Employee must exist                     |
| getEmployeeEntriesByDateRange()    | Filter entries by date   | Date range validation                   |
| getActiveSession(employeeId)       | Check current session    | Returns null if not clocked in          |
| isClockedIn(employeeId)            | Quick status check       | Boolean return                          |
| getAllPendingEntries()             | Manager view             | All pending entries across company      |
| getTodayEntries(employeeId)        | Today's activity         | Convenience method                      |
```

**Key Business Rules:**
- ✅ Only one active session per employee
- ✅ Cannot clock out without clocking in
- ✅ All entries start with PENDING status
- ✅ Clock-out time must be after clock-in time

### 2. TimesheetService
**Purpose:** Timesheet submission and manager approval workflow.

```
|                   Method                        |         Description         |              Business Rules            |
|-------------------------------------------------|-----------------------------|----------------------------------------|
| submitTimesheet(employeeId, startDate, endDate) | Create and submit timesheet | Entries must exist, no duplicates      |
| approveTimesheet(timesheetId, managerId)        | Manager approval            | Must be PENDING, auto-approves entries |
| rejectTimesheet(timesheetId, managerId, reason) | Manager rejection           | Must be PENDING, stores reason         |
| getEmployeeTimesheets(employeeId)               | Get all employee timesheets | Employee must exist                    |
| getEmployeeTimesheetsByDateRange()              | Filter by date range        | For payroll/reporting                  |
| getAllPendingTimesheets()                       | Manager dashboard           | All pending across company             |
| getPendingTimesheetsByDepartment()              | Department view             | For department managers                |
| getApprovedTimesheetsForPayroll()               | Payroll processing          | Returns only approved                  |
| getTimesheetStatistics(employeeId)              | Employee analytics          | Returns counts by status               |
```

**Timesheet Workflow:**
```
DRAFT → PENDING → APPROVED
      ↘ REJECTED → DRAFT (for editing)
```

**Key Business Rules:**
- ✅ Only PENDING timesheets can be approved/rejected
- ✅ Timesheet must contain at least one time entry
- ✅ No duplicate timesheets for same period
- ✅ All time entries must be PENDING when submitted
- ✅ Approving timesheet auto-approves all entries

### 3. PTOService
**Purpose:** Leave request management and approval workflow.

```
|                        Method                            |       Description         |         Business Rules          |
|----------------------------------------------------------|---------------------------|---------------------------------|
| requestPTO(employeeId, startDate, endDate, type, reason) | Create leave request      | Valid dates, no conflicts       |
| requestPTOWithPartial()                                  | Partial day support       | For half-day requests           |
| approvedPTO(requestId, managerId)                        | Manager approval          | Must be PENDING, conflict check |
| rejectPTO(requestId, managerId, reason)                  | Manager rejection         | Must be PENDING                 |
| cancelPTORequest(requestId)                              | Employee withdrawal       | Only PENDING can be cancelled   |
| getEmployeeRequests(employeeId)                          | Get all employee requests | Employee must exist             |
| getEmployeeRequestsByStatus()                            | Filter by status          | For employee dashboard          |
| getAllPendingRequests()                                  | Manager dashboard         | All pending across company      |
| getPendingRequestsByDepartment()                         | Department view           | For department managers         |
| getTotalPTODaysTaken(employeeId, year)                   | Leave balance             | Calculate used PTO              |
| hasOverlappingRequest()                                  | Conflict detection        | Prevent double-booking          |
| getPTOStatistics(employeeId)                             | Employee analytics        | Returns counts by status        |
```

**PTO Request Workflow:**
```
PENDING → APPROVED
        ↘ REJECTED
        ↘ CANCELLED
```

**Key Business Rules:**
- ✅ Cannot request leave for past dates
- ✅ Start date must be before end date
- ✅ Cannot have overlapping approved requests
- ✅ Only PENDING requests can be approved/rejected/cancelled
- ✅ Managers must have appropriate role (enforced in controller)

