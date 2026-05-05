# Employee Tracker - Exception Handling

This package contains custom exceptions and global exception handling.

## Exception Overview

```md
|           *Exception*           |  *HTTP Status*  |                  *When Used*                  |
|---------------------------------|-----------------|-----------------------------------------------|
| **ResourceNotFoundException**   | 404 Not Found   | Employee, Timesheet, or PTO request not found |
| **BadRequestException**         | 400 Bad Request | Invalid input or business rule violation      |
| **UnauthorizedActionException** | 403 Forbidden   | Permission denied (future security layer)     |
| **ConflictException**           | 409 Conflict    | Data conflict (duplicate, overlap)            |
```

## 🎯 GlobalExceptionHandler

Centralizes error handling across all controllers:

- Intercepts all exceptions
- Returns consistent JSON error responses
- Maps exceptions to appropriate HTTP status codes

## 📝 Error Response Format

```json
{
    "timestamp": "2026-05-04T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Employee is already clocked in",
    "path": "uri:/api/time/clock-in/",
    "success": false
}
```

## 🔧 Exception Flow

```
Service Layer
      ↓ throws
Custom Exception
      ↓ caught by
GlobalExceptionHandler
      ↓ returns
Consistent Error Response to Client
```

## ✅ Benefits

- Clean controllers (no try-catch blocks)
- Standardized error responses
- Proper HTTP status codes
- Frontend-friendly error messages