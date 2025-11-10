package use_case.port.incoming;

import use_case.dto.TaskUpdateCommand;

/**
 * Allows adapters to mutate or remove existing tasks.
 */
public interface TaskEditingUseCase {
    void updateTask(TaskUpdateCommand command);

    void deleteTask(String taskId);
}
