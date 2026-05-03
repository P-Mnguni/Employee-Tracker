package employee.tracker.mapper;

import employee.tracker.dto.PTOResponse;
import employee.tracker.model.PTORequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * PTORequestMapper - Converts PTORequest entities to PTOResponse DTOs
 * 
 * This mapper breaks the circular reference by only extracting the
 * employeeId instead of the full Employee object.
 */
@Component
public class PTORequestMapper {
    
    /**
     * Convert a single PTORequest entity to PTOResponse DTO
     * 
     * @param entity The PTORequest entity from database
     * @return Clean PTOResponse DTO for API response
     */
    public PTOResponse toResponse(PTORequest entity) {
        if (entity == null) return null;

        PTOResponse response = new PTOResponse();
        response.setId(entity.getId());
        response.setStartDate(entity.getStartDate());
        response.setEndDate(entity.getEndDate());
        response.setStatus(entity.getStatus());
        response.setType(entity.getLeaveType());
        response.setReason(entity.getReason());
        response.setRequestedAt(entity.getRequestedAt());
        response.setDaySRequested(entity.getDaysRequested());
        response.setIsPartialDay(entity.getIsPartialDay());
        response.setNotes(entity.getNotes());
        response.setRejectionReason(entity.getRejectionReason());

        // Extract only the employee ID (not the full object)
        if (entity.getEmployee() != null) {
            response.setEmployeeId(entity.getEmployee().getId());
        }

        // Extract approval information
        if (entity.getApprovedAt() != null) {
            response.setApprovedAt(entity.getApprovedAt());
        }

        if (entity.getApprovedBy() != null) {
            response.setApprovedBy(entity.getApprovedBy().getName());
        }

        return response;
    }

    /**
     * Convert a list of PTORequest entities to PTOResponse DTOs
     * 
     * @param entities List of PTORequest entities
     * @return List of clean PTOResponse DTOs
     */
    public List<PTOResponse> toResponseList(List<PTORequest> entities) {
        if (entities == null) return null;

        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * Convert a list of PTORequest entities to PTOResponse DTOs with summary only
     * (excludes notes and detailed rejection reason)
     * 
     * @param entities List of PTORequest entities
     * @return List of summarized PTOResponse DTOs
     */
    public List<PTOResponse> toSummaryList(List<PTORequest> entities) {
        if (entities == null) return null;

        return entities.stream().map(entity -> {
            PTOResponse response = toResponse(entity);
            response.setNotes(null);                // Clear detailed fields for summary view
            return response;
        })
        .collect(Collectors.toList());
    }
}
