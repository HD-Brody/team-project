package use_case.service;

import use_case.dto.TaskUpdateCommand;
import use_case.port.incoming.TaskEditingUseCase;
import use_case.repository.TaskRepository;
import java.util.Objects;

/**
 * Handles updates to user-managed tasks.
 */
public class TaskEditingService implements TaskEditingUseCase {
    private final TaskRepository taskRepository;

    public TaskEditingService(TaskRepository taskRepository) {
        this.taskRepository = Objects.requireNonNull(taskRepository, "taskRepository");
    }

    @Override
    public void updateTask(TaskUpdateCommand command) {
        // TODO: load, mutate, validate, and persist the task aggregate.
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteTask(String taskId) {
        // TODO: delegate to repository after verifying authorization.
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
