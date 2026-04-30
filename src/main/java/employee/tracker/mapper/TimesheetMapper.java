package employee.tracker.mapper;

import employee.tracker.dto.TimesheetResponse;
import employee.tracker.dto.TimeEntryResponse;
import employee.tracker.model.Timesheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TimesheetMapper - Converts Timesheet entities to TimesheetResponse DTOs
 * 
 * This mapper uses TimeEntryMapper to convert time entries, ensuring
 * no circular references occur in the response.
 */
@Component
public class TimesheetMapper {
    
    @Autowired
    private TimeEntryMapper timeEntryMapper;

    /**
     * Convert a single Timesheet entity to TimesheetResponse DTO
     * 
     * @param entity The Timesheet entity from database
     * @return Clean TimesheetResponse DTO for API response
     */
    public TimesheetResponse toResponse(Timesheet entity) {
        if (entity == null) return null;

        TimesheetResponse response = new TimesheetResponse();
        response.setId(entity.getId());
        response.setStartDate(entity.getStartDate());
        response.setEndDate(entity.getEndDate());
        response.setStatus(entity.getStatus());
        response.setSubmittedAt(entity.getSubmittedAt());

        // Extract only the employee ID (not the full object)
        if (entity.getEmployee() != null) {
            response.setEmployeeId(entity.getEmployee().getId());
        }

        // Convert time entries using TimeEntryMapper
        if (entity.getTimeEntries() != null && !entity.getTimeEntries().isEmpty()) {
            List<TimeEntryResponse> entryResponses = timeEntryMapper.toResponseList(entity.getTimeEntries());
            response.setEntries(entryResponses);

            // Calculate total hours from entries
            double totalHours = entryResponses.stream()
                                .mapToDouble(entry -> entry.getTotalHours() != null ? entry.getTotalHours() : 0.0).sum();
            response.setTotalHours(Math.round(totalHours * 10) / 10.0);
        }

        return response;
    }

    /**
     * Convert a list of Timesheet entities to TimesheetResponse DTOs
     * 
     * @param entities List of Timesheet entities
     * @return List of clean TimesheetResponse DTOs
     */
    public List<TimesheetResponse> toResponseList(List<Timesheet> entities) {
        if (entities == null) return null;

        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
