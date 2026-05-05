package employee.tracker.mapper;

import employee.tracker.dto.TimeEntryResponse;
import employee.tracker.model.TimeEntry;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TimeEntryMapper - Converts TimeEntry entities to TimeEntryResponse DTOs
 * 
 * This mapper breaks the circular reference by only extracting the 
 * necessary fields (employeeId instead of full Employee object).
 */
@Component
public class TimeEntryMapper {
    
    /**
     * Convert a single TimeEntry entity to TimeEntryResponse DTO
     * 
     * @param entity The TimeEntry entity from database
     * @return Clean TimeEntryResponse DTO for API response
     */
    public TimeEntryResponse toResponse(TimeEntry entity) {
        if (entity == null) return null;

        TimeEntryResponse response = new TimeEntryResponse();
        response.setId(entity.getId());
        response.setClockInTime(entity.getClockInTime());
        response.setClockOutTime(entity.getClockOutTime());
        response.setStatus(entity.getStatus());

        // Extract only the employee ID (not the full object)
        if (entity.getEmployee() != null) {
            response.setEmployeeId(entity.getEmployee().getId());
        }

        // Calculate total hours if clocked out
        if (entity.getClockInTime() != null && entity.getClockOutTime() != null) {
            double hours = Duration.between(entity.getClockInTime(), entity.getClockOutTime()).toMinutes() / 60.0;
            response.setTotalHours(Math.round(hours * 10) / 10.0);      // Round to 1 decimal
        }

        return response;
    }

    /**
     * Convert a list of TimeEntry entities to TimeEntryResponse DTOs
     * 
     * @param entities List of TimeEntry entities
     * @return List of clean TimeEntryResponse DTOs
     */
    public List<TimeEntryResponse> toResponseList(List<TimeEntry> entities) {
        if (entities == null) return null;

        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
