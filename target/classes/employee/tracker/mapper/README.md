# Employee Tracker - Mapper Layer

This package contains all mapper classes that convert entities to response DTOs.

## 📊 Mapper Overview

```md
|       *Mapper*      |       *Source → Target*       |        *Key Methods*       |
|---------------------|-------------------------------|----------------------------|
| **TimeEntryMapper** | TimeEntry → TimeEntryResponse | toResponse, toResponseList |
| **TimesheetMapper** | Timesheet → TimesheetResponse | toResponse, toResponseList |
| **PTOMapper**       | PTORequest → PTOResponse      | toResponse, toResponseList |
```

## 🎯 What Mappers Do

- Convert entities to DTOs for API responses
- Extract only employeeId (not full Employee object)
- Break circular references
- Keep controllers clean and focused on HTTP concerns

## 🧠 Mapper Chain

```
TimesheetMapper
|-- uses TimeEntryMapper
|-- converts TimeEntry entities to TimeEntryResponse DTOs
```

## ✅ Design Principles

- **Single responsibility** - Only convert, no business logic
- **Reusable** - One mapper used across multiple controllers
- **Null-safe** - Handle null inputs gracefully
- **Spring Components** - Injectable and testable

## 🔧 Usage Example

```java
@Component
public class TimesheetMapper {

    @Autowired
    private TimeEntryMapper timeEntryMapper;

    public TimesheetResponse toResponse(Timesheet entity) {
        TimesheetResponse response = new TimesheetResponse();
        response.setId(entity.getId());
        response.setEmployeeId(entity.getEmployee().getId());
        response.setEntries(timeEntryMapper.toResponseList(entity.getTimeEntries()));
        return response;
    }
}
```