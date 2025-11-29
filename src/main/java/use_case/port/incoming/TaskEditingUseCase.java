package use_case.port.incoming;

import use_case.dto.TaskCreationCommand;
import use_case.dto.TaskUpdateCommand;
import entity.Task;
import entity.TaskStatus;
import java.util.List;
import java.util.Optional;

/**
 * Allows adapters to create, view, mutate, or remove tasks.
 */
public interface TaskEditingUseCase {
    Task createTask(TaskCreationCommand command);
    List<Task> listTasksForUser(String userId, TaskStatus statusFilter);
    Optional<Task> getTaskById(String taskId);
    void updateTask(TaskUpdateCommand command);
    void deleteTask(String taskId);
}
