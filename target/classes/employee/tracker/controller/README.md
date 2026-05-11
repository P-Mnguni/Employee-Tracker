# Employee Tracker - Controller Layer (REST API)

This package contains all the REST API controllers that expose the Employee Tracker System's functionality to clients (web apps, mobile apps, Postman, etc.).

## 📊 Controller Overview

### Controller Classes (3)

```md
| Controller | Base Route | Purpose | Key Endpoints |
|------------|------------|---------|---------------|
| **TimeEntryController** | `/api/time` | Time tracking & clock management | clock-in, clock-out, active session |
| **TimesheetController** | `/api/timesheets` | Timesheet workflow & approvals | submit, approve, reject, pending |
| **PTOController** | `/api/pto` | Leave request management | request, approve, reject, balance |
```

## 🗄️ Controller Details

### 1. TimeEntryController
**Base Route:** `/api/time`

**Purpose:** Handles all clock-in/clock-out operations and time entry management.

```md
| Method | Endpoint | Description | Input |
|--------|----------|-------------|-------|
| POST | `/clock-in` | Employee starts work | `employeeId` |
| POST | `/clock-out` | Employee ends work | `employeeId` |
| POST | `/clock-out-with-time` | Manual clock-out with specific time | `employeeId`, `clockOutTime` |
| GET | `/employee/{employeeId}` | Get all entries for employee | Path variable |
| GET | `/employee/{employeeId}/date-range` | Filter entries by date range | `startDate`, `endDate` |
| GET | `/employee/{employeeId}/active-session` | Get current active session | Path variable |
| GET | `/employee/{employeeId}/status` | Check if clocked in | Path variable |
| GET | `/employee/{employeeId}/today` | Get today's entries | Path variable |
| GET | `/pending` | All pending entries (manager) | None |
| GET | `/employee/{employeeId}/pending` | Employee's pending entries | Path variable |
```

**Key Features:**
- ✅ Double clock-in prevention
- ✅ Active session detection
- ✅ Date range filtering
- ✅ Manager pending view

### 2. TimesheetController
**Base Route:** `/api/timesheets`

**Purpose:** Handles timesheet submission, approval workflow, and payroll preparation.

```md
| Method | Endpoint | Description | Input |
|--------|----------|-------------|-------|
| POST | `/submit` | Submit timesheet | `employeeId`, `startDate`, `endDate` |
| PUT | `/{timesheetId}/approve` | Approve timesheet | `timesheetId`, `managerId` |
| PUT | `/{timesheetId}/reject` | Reject timesheet | `timesheetId`, `managerId`, `reason` |
| GET | `/employee/{employeeId}` | Get employee timesheets | Path variable |
| GET | `/employee/{employeeId}/date-range` | Filter by date range | `startDate`, `endDate` |
| GET | `/pending` | All pending timesheets (manager) | None |
| GET | `/pending/department` | Pending by department | `departmentName` |
| GET | `/{timesheetId}` | Get timesheet by ID | Path variable |
| GET | `/employee/{employeeId}/statistics` | Timesheet statistics | Path variable |
| GET | `/exists` | Check if timesheet exists | `employeeId`, `startDate`, `endDate` |
```

**Timesheet Workflow:**
```
DRAFT → PENDING → APPROVED
↘ REJECTED → DRAFT (for editing)
```

**Key Features:**
- ✅ Automatic time entry approval when timesheet approved
- ✅ Department filtering for managers
- ✅ Duplicate period prevention
- ✅ Submission tracking

### 3. PTOController
**Base Route:** `/api/pto`

**Purpose:** Handles leave requests, approvals, and balance tracking.

```md
| Method | Endpoint | Description | Input |
|--------|----------|-------------|-------|
| POST | `/request` | Request PTO | `employeeId`, `startDate`, `endDate`, `type`, `reason` |
| POST | `/request-with-partial` | Request with partial day support | + `isPartialDay`, `daysRequested` |
| PUT | `/{requestId}/approve` | Approve request | `requestId`, `managerId` |
| PUT | `/{requestId}/reject` | Reject request | `requestId`, `managerId`, `reason` |
| PUT | `/{requestId}/cancel` | Cancel request | `requestId` |
| GET | `/employee/{employeeId}` | Get employee requests | Path variable |
| GET | `/employee/{employeeId}/status` | Filter by status | `status` |
| GET | `/employee/{employeeId}/type` | Filter by leave type | `leaveType` |
| GET | `/pending` | All pending requests (manager) | None |
| GET | `/pending/department` | Pending by department | `departmentName` |
| GET | `/{requestId}` | Get request by ID | Path variable |
| GET | `/employee/{employeeId}/balance` | Get PTO balance | `year` |
| GET | `/employee/{employeeId}/statistics` | Request statistics | Path variable |
| GET | `/employee/{employeeId}/has-conflict` | Check overlapping requests | `startDate`, `endDate` |
```

**PTO Workflow:**
```
PENDING → APPROVED
↘ REJECTED
↘ CANCELLED
```

**Key Features:**
- ✅ Conflict detection (no double-booking)
- ✅ Past date prevention
- ✅ Partial day support (0.5 days)
- ✅ Leave balance calculation
- ✅ Department filtering
- ✅ Different leave types (PTO, SICK, UNPAID)

## 🔄 API Response Format

All endpoints return JSON responses with a consistent structure:

### Success Response:
```json
{
  "success": true,
  "message": "Operation completed successfully",
  // Additional data fields specific to endpoint
}
```

### Error Response:
```json
{
  "success": false,
  "error": "Description of what went wrong"
}
```

### HTTP Status Codes Used:
```md
|        Status Code        |      Meaning       |       When Used       |
|---------------------------|--------------------|-----------------------|
| 200 OK                    | Success            | GET, PUT operations   |
| 201 CREATED               | Resource created   | POST operations       |
| 400 BAD REQUEST           | Invalid input      | Validation failures   |
| 404 NOT FOUND             | Resource not found | Invalid ID            |
| 500 INTERNAL SERVER ERROR | Server error       | Unexpected exceptions |
```

## 🧪 Testing the APIs
### Prerequisites
1. Application running on http://localhost:8080
2. Mock data loaded (automatic on first run)
3. Postman or similar API client

### Sample Test Cases

TimeEntryController Tests:
```bash
# 1. Clock in (John Doe)
POST http://localhost:8080/api/time/clock-in?employeeId=4

# 2. Check active session
GET http://localhost:8080/api/time/employee/4/active-session

# 3. Clock out
POST http://localhost:8080/api/time/clock-out?employeeId=4

# 4. Get today's entries
GET http://localhost:8080/api/time/employee/4/today
```

TimesheetController Tests:
```bash
# 1. Submit timesheet
POST http://localhost:8080/api/timesheets/submit?employeeId=4&startDate=2024-04-18&endDate=2024-04-24

# 2. View pending (as manager)
GET http://localhost:8080/api/timesheets/pending

# 3. Approve timesheet
PUT http://localhost:8080/api/timesheets/1/approve?managerId=2

# 4. Get employee timesheets
GET http://localhost:8080/api/timesheets/employee/4
```

PTOController Tests:
```bash
# 1. Request PTO
POST http://localhost:8080/api/pto/request?employeeId=4&startDate=2024-05-10&endDate=2024-05-15&type=PTO&reason=Vacation

# 2. View pending (as manager)
GET http://localhost:8080/api/pto/pending

# 3. Approve request
PUT http://localhost:8080/api/pto/1/approve?managerId=2

# 4. Get PTO balance
GET http://localhost:8080/api/pto/employee/4/balance?year=2024

# 5. Check for conflicts
GET http://localhost:8080/api/pto/employee/4/has-conflict?startDate=2024-05-10&endDate=2024-05-15
```

## 📊 Test Data Summary
When you run the application with DataLoader, the following test data is available:

Employees:
```
| ID |      Name      | Department  |   Role   |
|----|----------------|-------------|----------|
| 1  | John Admin     |      IT     | ADMIN    |
| 2  | Sarah Manager  | Engineering | MANAGER  |
| 3  | Mike Manager   |    Sales    | MANAGER  |
| 4  | John Doe       | Engineering | EMPLOYEE |
| 5  | Jane Smith     | Engineering | EMPLOYEE |
| 6  | Bob Johnson    |    Sales    | EMPLOYEE |
| 7  | Alice Brown    |    Sales    | EMPLOYEE |
| 8  | Charlie Wilson |  Marketing  | EMPLOYEE |
```

Active Sessions:
- John Doe (ID 4) - Clocked in today ✅
- Bob Johnson (ID 6) - Clocked in today ✅
- Sarah Manager (ID 2) - Clocked in today ✅

Pending Timesheets:
- John Doe (ID 4) - Last week pending approval

Pending PTO Requests:
- John Doe (ID 4) - Future PTO request
- Jane Smith (ID 5) - Future PTO request
- Bob Johnson (ID 6) - Future PTO request

## 🛠️ Design Principles
```
|       Principle        |                   Implementation                        |
|------------------------|---------------------------------------------------------|
| RESTful Design         | Proper HTTP methods and status codes                    |
| Separation of Concerns | Controllers only handle HTTP, no business logic         |
| Consistent Responses   | Standard JSON format with success/error fields          |
| Descriptive Endpoints  | Clear, semantic URL paths                               |
| Proper Error Handling  | Meaningful error messages with appropriate status codes |
```

## 🔜 Future Improvements
- Add DTOs to control serialization (reduce circular reference issues)
- Add global exception handler with @ControllerAdvice
- Add API versioning (e.g., /api/v1/time)
- Add request/response validation with @Valid
- Add pagination for large result sets
- Add sorting and filtering options
- Add HATEOAS links
- Add API documentation with Swagger/OpenAPI
- Add rate limiting