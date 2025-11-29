package interface_adapter.inbound.web.dto;

import entity.Task;
import entity.TaskStatus;

/**
 * Response DTO for task data to web/GUI layer.
 * Converts domain entity to presentation format.
 * Can be enriched with additional display data without affecting domain.
 */
public class TaskResponse {
    private final String taskId;
    private final String userId;
    private final String courseId;
    private final String assessmentId;
    private final String title;
    private final String dueAt;  // ISO-8601 string for web display
    private final Integer estimatedEffortMins;
    private final Integer priority;
    private final TaskStatus status;
    private final String notes;

    public TaskResponse(Task task) {
        this.taskId = task.getTaskId();
        this.userId = task.getUserId();
        this.courseId = task.getCourseId();
        this.assessmentId = task.getAssessmentId();
        this.title = task.getTitle();
        this.dueAt = task.getDueAt() != null ? task.getDueAt().toString() : null;
        this.estimatedEffortMins = task.getEstimatedEffortMins();
        this.priority = task.getPriority();
        this.status = task.getStatus();
        this.notes = task.getNotes();
    }

    // Getters only (immutable)
    public String getTaskId() {
        return taskId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public String getTitle() {
        return title;
    }

    public String getDueAt() {
        return dueAt;
    }

    public Integer getEstimatedEffortMins() {
        return estimatedEffortMins;
    }

    public Integer getPriority() {
        return priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }
}

