package infrastructure;

import entity.Task;
import use_case.repository.TaskRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of TaskRepository for testing and demo purposes.
 * 
 * This eliminates code duplication across test files and demo applications.
 * When Leo's SqliteTaskRepositoryAdapter is ready, applications should use that instead.
 * 
 * Thread-Safety: Not thread-safe. For single-threaded tests only.
 */
public class InMemoryTaskRepository implements TaskRepository {
    
    private final Map<String, Task> tasks = new HashMap<>();

    @Override
    public Optional<Task> findById(String taskId) {
        return Optional.ofNullable(tasks.get(taskId));
    }

    @Override
    public List<Task> findByUserId(String userId) {
        return tasks.values().stream()
                .filter(task -> task.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void save(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    @Override
    public void deleteById(String taskId) {
        tasks.remove(taskId);
    }
    
    /**
     * Clears all tasks from the repository.
     * Useful for resetting state between tests.
     */
    public void clear() {
        tasks.clear();
    }
    
    /**
     * Returns the number of tasks currently stored.
     * Useful for test assertions.
     */
    public int size() {
        return tasks.size();
    }
}

