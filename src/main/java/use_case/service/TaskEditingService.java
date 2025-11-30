package use_case.service;

import entity.Assessment;
import entity.AssessmentType;
import use_case.dto.TaskCreationCommand;
import use_case.dto.TaskUpdateCommand;
import use_case.port.incoming.TaskEditingUseCase;
import use_case.repository.AssessmentRepository;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Handles creating, viewing, updating, and deleting user-managed assessments (formerly tasks).
 */
public class TaskEditingService implements TaskEditingUseCase {
    private final AssessmentRepository assessmentRepository;

    public TaskEditingService(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository, "assessmentRepository");
    }

    @Override
    public Assessment createTask(TaskCreationCommand command) {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(command.getCourseId(), "courseId");
        Objects.requireNonNull(command.getTitle(), "title");
        
        // Generate new assessment ID
        String assessmentId = UUID.randomUUID().toString();
        
        // Convert Instant to ISO string for endsAt (due date)
        String endsAt = command.getDueAt() != null 
            ? command.getDueAt().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            : null;
        
        // Create new assessment entity
        // Map task fields to assessment fields:
        // - estimatedEffortMins -> durationMinutes
        // - status stored in notes field as "Status: TODO" etc.
        // - priority ignored (not in Assessment entity)
        Assessment newAssessment = new Assessment(
                assessmentId,
                command.getCourseId(),
                command.getTitle(),
                AssessmentType.OTHER, // Default type for user-created tasks
                0.0, // grade - default to 0
                null, // startsAt - user tasks don't have start time
                endsAt, // endsAt is the due date
                command.getEstimatedEffortMins() != null ? command.getEstimatedEffortMins().longValue() : null,
                null, // weight - user tasks don't have weight
                "", // location - empty for user tasks
                formatNotesWithStatus(command.getNotes(), command.getStatus())
        );
        
        // Save to repository
        assessmentRepository.save(newAssessment);
        
        return newAssessment;
    }

    @Override
    public List<Assessment> listTasksForUser(String courseId, entity.TaskStatus statusFilter) {
        Objects.requireNonNull(courseId, "courseId");
        List<Assessment> assessments = assessmentRepository.findByCourseId(courseId);

        if (statusFilter != null) {
            return assessments.stream()
                    .filter(assessment -> extractStatusFromNotes(assessment.getNotes()) == statusFilter)
                    .collect(Collectors.toList());
        }
        return assessments;
    }

    @Override
    public Optional<Assessment> getTaskById(String assessmentId) {
        Objects.requireNonNull(assessmentId, "assessmentId");
        return assessmentRepository.findById(assessmentId);
    }

    @Override
    public void updateTask(TaskUpdateCommand command) {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(command.getTaskId(), "assessmentId");

        Assessment existingAssessment = assessmentRepository.findById(command.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Assessment not found: " + command.getTaskId()));

        String updatedEndsAt = command.getDueAt() != null 
            ? command.getDueAt().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            : existingAssessment.getEndsAt();
        
        String updatedNotes = command.getNotes() != null || command.getStatus() != null
            ? formatNotesWithStatus(
                command.getNotes() != null ? command.getNotes() : extractNotesWithoutStatus(existingAssessment.getNotes()),
                command.getStatus() != null ? command.getStatus() : extractStatusFromNotes(existingAssessment.getNotes()))
            : existingAssessment.getNotes();

        Assessment updatedAssessment = new Assessment(
                existingAssessment.getAssessmentId(),
                existingAssessment.getCourseId(),
                command.getTitle() != null ? command.getTitle() : existingAssessment.getTitle(),
                existingAssessment.getType(),
                existingAssessment.getGrade(),
                existingAssessment.getStartsAt(),
                updatedEndsAt,
                command.getEstimatedEffortMins() != null ? command.getEstimatedEffortMins().longValue() 
                    : existingAssessment.getDurationMinutes(),
                existingAssessment.getWeight(),
                existingAssessment.getLocation(),
                updatedNotes
        );

        assessmentRepository.update(updatedAssessment);
    }

    @Override
    public void deleteTask(String assessmentId) {
        Objects.requireNonNull(assessmentId, "assessmentId");
        assessmentRepository.deleteById(assessmentId);
    }
    
    // Helper methods for encoding/decoding TaskStatus in notes field
    private String formatNotesWithStatus(String notes, entity.TaskStatus status) {
        String statusLine = "[Status: " + (status != null ? status.toString() : "TODO") + "]";
        if (notes == null || notes.isBlank()) {
            return statusLine;
        }
        return statusLine + "\n" + notes;
    }
    
    private entity.TaskStatus extractStatusFromNotes(String notes) {
        if (notes == null || !notes.contains("[Status:")) {
            return entity.TaskStatus.TODO;
        }
        try {
            int start = notes.indexOf("[Status: ") + 9;
            int end = notes.indexOf("]", start);
            if (start > 8 && end > start) {
                String statusStr = notes.substring(start, end);
                return entity.TaskStatus.valueOf(statusStr);
            }
        } catch (Exception e) {
            // If parsing fails, default to TODO
        }
        return entity.TaskStatus.TODO;
    }
    
    private String extractNotesWithoutStatus(String notes) {
        if (notes == null || !notes.contains("[Status:")) {
            return notes;
        }
        int statusLineEnd = notes.indexOf("\n");
        if (statusLineEnd > 0 && statusLineEnd < notes.length() - 1) {
            return notes.substring(statusLineEnd + 1);
        }
        return "";
    }
}
