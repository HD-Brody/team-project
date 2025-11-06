package domain.repository;

import domain.model.Task;
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
