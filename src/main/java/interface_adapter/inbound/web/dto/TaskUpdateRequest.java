package interface_adapter.inbound.web.dto;

import entity.TaskStatus;

/**
 * Request DTO for task updates from web/GUI layer.
 * Separates web API from use case layer (Clean Architecture).
 * 
 * All fields are optional - null values preserve existing task data.
 */
public class TaskUpdateRequest {
    private String title;
    private String dueAt;  // ISO-8601 string from web (e.g., "2025-11-20T23:59:00Z")
    private Integer estimatedEffortMins;
    private Integer priority;
    private TaskStatus status;
    private String notes;

    public TaskUpdateRequest() {
    }

    public TaskUpdateRequest(String title, String dueAt, Integer estimatedEffortMins,
                             Integer priority, TaskStatus status, String notes) {
        this.title = title;
        this.dueAt = dueAt;
        this.estimatedEffortMins = estimatedEffortMins;
        this.priority = priority;
        this.status = status;
        this.notes = notes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDueAt() {
        return dueAt;
    }

    public void setDueAt(String dueAt) {
        this.dueAt = dueAt;
    }

    public Integer getEstimatedEffortMins() {
        return estimatedEffortMins;
    }

    public void setEstimatedEffortMins(Integer estimatedEffortMins) {
        this.estimatedEffortMins = estimatedEffortMins;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

