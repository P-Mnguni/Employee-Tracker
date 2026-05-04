# Employee Tracker - DTO Layer (Data Transfer Objects)

This package contains all Request and Response DTOs that define the API 
contract between the backend and frontend.

## DTO Overview

### Request DTOs

```md
|        *DTO*         |       *Purpose*       |                *Key Fields*                  |
|----------------------|-----------------------|----------------------------------------------|
| **ClockInRequest**   | Clock-in API request  | employeeId, gpsLocation                      |
| **ClockOutRequest**  | Clock-out API request | employeeId, notes                            |
| **TimesheetRequest** | Timesheet submission  | employeeId, startDate, endDate               |
| **PTORequestDTO**    | PTO request           | employeeId, startDate, endDate, type, reason |
```

### Response DTOs

```md
|        *DTO*          |      *Purpose*      |                    *Key Fields*                     |
|-----------------------|---------------------|-----------------------------------------------------|
| **TimeEntryResponse** | Time entry response | id, clockOutTime, clockOutTime, status, employeeId  |
| **TimesheetResponse** | Timesheet response  | id, startDate, endDate, status, employeeId, entries |
| **PTOResponse**       | PTO response        | id, startDate, endDate, type, status, employeeId    |
```

## 🎯 Why DTOs Matter

- **Prevent circular references** - No bidirectional relationships in responses
- **Control data exposure** - Only send what the frontend needs
- **API versioning** - Change DTOs without affecting entities
- **Performance** - Smaller payload sizes

## 🔄️ DTO Hierarchy

```
TimesheetResponse
|-- employeeId (Long)
|-- entries (List<TimeEntryResponse>)
|-- employeeId (Long)
|-- clockInTime
|-- clockOutTime
|-- status
```

## ✅ Design Principles

- ❌ No entity relationships
- ❌ No business logic
- ❌ No nested entity objects (use IDs instead)
- ✔️ Flat, simple structure
- ✔️ Only necessary fields