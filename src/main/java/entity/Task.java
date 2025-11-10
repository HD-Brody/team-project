package entity;

import java.time.Instant;
import java.util.Objects;

/**
 * A planning task derived from assessments or user input.
 */
public final class Task {
    private final String taskId;
    private final String userId;
    private final String courseId;
    private final String assessmentId;
    private final String title;
    private final Instant dueAt;
    private final Integer estimatedEffortMins;
    private final Integer priority;
    private final TaskStatus status;
    private final String notes;

    public Task(String taskId, String userId, String courseId, String assessmentId, String title,
                Instant dueAt, Integer estimatedEffortMins, Integer priority, TaskStatus status,
                String notes) {
        this.taskId = Objects.requireNonNull(taskId, "taskId");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.courseId = Objects.requireNonNull(courseId, "courseId");
        this.assessmentId = assessmentId;
        this.title = Objects.requireNonNull(title, "title");
        this.dueAt = dueAt;
        this.estimatedEffortMins = estimatedEffortMins;
        this.priority = priority;
        this.status = Objects.requireNonNull(status, "status");
        this.notes = notes;
    }

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

    public Instant getDueAt() {
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
