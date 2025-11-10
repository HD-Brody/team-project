package use_case.dto;

import entity.TaskStatus;
import java.time.Instant;
import java.util.Objects;

/**
 * Command for editing an existing task.
 */
public final class TaskUpdateCommand {
    private final String taskId;
    private final String title;
    private final Instant dueAt;
    private final Integer estimatedEffortMins;
    private final Integer priority;
    private final TaskStatus status;
    private final String notes;

    public TaskUpdateCommand(String taskId, String title, Instant dueAt, Integer estimatedEffortMins,
                             Integer priority, TaskStatus status, String notes) {
        this.taskId = Objects.requireNonNull(taskId, "taskId");
        this.title = title;
        this.dueAt = dueAt;
        this.estimatedEffortMins = estimatedEffortMins;
        this.priority = priority;
        this.status = status;
        this.notes = notes;
    }

    public String getTaskId() {
        return taskId;
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
