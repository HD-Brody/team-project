package use_case.dto;

import java.time.Instant;
import java.util.Objects;

/**
 * Persistence-layer projection of a task that can be translated into a calendar entry.
 */
public final class ScheduledTaskSnapshot {
    private final String taskId;
    private final String userId;
    private final String courseId;
    private final String title;
    private final Instant dueAt;
    private final Integer estimatedEffortMins;
    private final Double weightPercent;
    private final String location;
    private final String notes;

    public ScheduledTaskSnapshot(String taskId, String userId, String courseId, String title,
                                 Instant dueAt, Integer estimatedEffortMins,
                                 Double weightPercent, String location, String notes) {
        this.taskId = Objects.requireNonNull(taskId, "taskId");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.courseId = courseId;
        this.title = Objects.requireNonNull(title, "title");
        this.dueAt = dueAt;
        this.estimatedEffortMins = estimatedEffortMins;
        this.weightPercent = weightPercent;
        this.location = location;
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

    public String getTitle() {
        return title;
    }

    public Instant getDueAt() {
        return dueAt;
    }

    public Integer getEstimatedEffortMins() {
        return estimatedEffortMins;
    }

    public Double getWeightPercent() {
        return weightPercent;
    }

    public String getLocation() {
        return location;
    }

    public String getNotes() {
        return notes;
    }
}
