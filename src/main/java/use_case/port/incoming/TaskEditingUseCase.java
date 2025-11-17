package use_case.port.incoming;

import use_case.dto.TaskUpdateCommand;
import entity.Task;
import entity.TaskStatus;
import java.util.List;
import java.util.Optional;

/**
 * Allows adapters to mutate or remove existing tasks.
 */
public interface TaskEditingUseCase {
    List<Task> listTasksForUser(String userId, TaskStatus statusFilter);
    void updateTask(TaskUpdateCommand command);
    Optional<Task> getTaskById(String taskId);
    void deleteTask(String taskId);
}
