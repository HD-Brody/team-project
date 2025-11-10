package use_case.repository;

import entity.Task;
import java.util.List;
import java.util.Optional;

/**
 * Persistence boundary for planning tasks.
 */
public interface TaskRepository {
    Optional<Task> findById(String taskId);

    List<Task> findByUserId(String userId);

    void save(Task task);

    void deleteById(String taskId);
}
