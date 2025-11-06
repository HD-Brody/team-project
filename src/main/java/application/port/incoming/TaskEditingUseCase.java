package application.port.incoming;

import application.dto.TaskUpdateCommand;

/**
 * Allows adapters to mutate or remove existing tasks.
 */
public interface TaskEditingUseCase {
    void updateTask(TaskUpdateCommand command);

    void deleteTask(String taskId);
}
