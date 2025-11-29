package interface_adapter.inbound.web.dto;

import entity.TaskStatus;

/**
 * Request DTO for creating new tasks from web/GUI layer.
 * Unlike TaskUpdateRequest, this requires userId and courseId.
 */
public class TaskCreationRequest {
    private String userId;
    private String courseId;
    private String assessmentId; // nullable
    private String title;
    private String dueAt;  // ISO-8601 string
    private Integer estimatedEffortMins;
    private Integer priority;
    private TaskStatus status;
    private String notes;

    public TaskCreationRequest() {
    }

    public TaskCreationRequest(String userId, String courseId, String assessmentId,
                               String title, String dueAt, Integer estimatedEffortMins,
                               Integer priority, TaskStatus status, String notes) {
        this.userId = userId;
        this.courseId = courseId;
        this.assessmentId = assessmentId;
        this.title = title;
        this.dueAt = dueAt;
        this.estimatedEffortMins = estimatedEffortMins;
        this.priority = priority;
        this.status = status;
        this.notes = notes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
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

