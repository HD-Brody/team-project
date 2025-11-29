package use_case.service;

import entity.Task;
import entity.TaskStatus;
import use_case.dto.TaskUpdateCommand;
import use_case.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TaskEditingServiceTest {
    private TaskRepository repository;
    private TaskEditingService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTaskRepository();
        service = new TaskEditingService(repository);
    }

    // Test: List all tasks
    @Test
    void shouldListAllTasksForUser() {
        String userId = "user-123";
        Task task1 = createTask("task-1", userId, "Task 1", TaskStatus.TODO);
        Task task2 = createTask("task-2", userId, "Task 2", TaskStatus.DONE);
        repository.save(task1);
        repository.save(task2);

        List<Task> tasks = service.listTasksForUser(userId, null);

        assertEquals(2, tasks.size());
    }

    // Test: Filter by status
    @Test
    void shouldFilterTasksByStatus() {
        String userId = "user-123";
        repository.save(createTask("task-1", userId, "Todo", TaskStatus.TODO));
        repository.save(createTask("task-2", userId, "Done", TaskStatus.DONE));

        List<Task> todoTasks = service.listTasksForUser(userId, TaskStatus.TODO);

        assertEquals(1, todoTasks.size());
        assertEquals("Todo", todoTasks.get(0).getTitle());
    }

    // Test: Get by ID
    @Test
    void shouldGetTaskById() {
        Task task = createTask("task-1", "user-123", "Test", TaskStatus.TODO);
        repository.save(task);

        Optional<Task> result = service.getTaskById("task-1");

        assertTrue(result.isPresent());
        assertEquals("Test", result.get().getTitle());
    }

    // Test: Update title
    @Test
    void shouldUpdateTaskTitle() {
        repository.save(createTask("task-1", "user-123", "Old Title", TaskStatus.TODO));

        TaskUpdateCommand command = new TaskUpdateCommand(
                "task-1", "New Title", null, null, null, null, null);
        service.updateTask(command);

        Task updated = repository.findById("task-1").get();
        assertEquals("New Title", updated.getTitle());
    }

    // Test: Update due date
    @Test
    void shouldUpdateTaskDueDate() {
        Instant originalDate = Instant.parse("2025-11-15T10:00:00Z");
        Instant newDate = Instant.parse("2025-11-20T10:00:00Z");
        repository.save(createTaskWithDate("task-1", "user-123", "Test", originalDate));

        TaskUpdateCommand command = new TaskUpdateCommand(
                "task-1", null, newDate, null, null, null, null);
        service.updateTask(command);

        assertEquals(newDate, repository.findById("task-1").get().getDueAt());
    }

    // Test: Keep existing values when null
    @Test
    void shouldKeepExistingValuesWhenCommandFieldsAreNull() {
        Instant date = Instant.parse("2025-11-15T10:00:00Z");
        repository.save(createTaskWithDate("task-1", "user-123", "Original", date));

        TaskUpdateCommand command = new TaskUpdateCommand(
                "task-1", null, null, null, null, null, null);
        service.updateTask(command);

        Task updated = repository.findById("task-1").get();
        assertEquals("Original", updated.getTitle());
        assertEquals(date, updated.getDueAt());
    }

    // Test: Update status
    @Test
    void shouldUpdateTaskStatus() {
        repository.save(createTask("task-1", "user-123", "Test", TaskStatus.TODO));

        TaskUpdateCommand command = new TaskUpdateCommand(
                "task-1", null, null, null, null, TaskStatus.DONE, null);
        service.updateTask(command);

        assertEquals(TaskStatus.DONE, repository.findById("task-1").get().getStatus());
    }

    // Test: Error when task not found
    @Test
    void shouldThrowExceptionWhenUpdatingNonexistentTask() {
        TaskUpdateCommand command = new TaskUpdateCommand(
                "nonexistent", "Title", null, null, null, null, null);

        assertThrows(IllegalArgumentException.class, () -> service.updateTask(command));
    }

    // Test: Delete task
    @Test
    void shouldDeleteTask() {
        repository.save(createTask("task-1", "user-123", "Test", TaskStatus.TODO));

        service.deleteTask("task-1");

        assertFalse(repository.findById("task-1").isPresent());
    }

    // Test: Error when deleting nonexistent
    @Test
    void shouldThrowExceptionWhenDeletingNonexistentTask() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteTask("nonexistent"));
    }

    // Helper methods
    private Task createTask(String taskId, String userId, String title, TaskStatus status) {
        return new Task(taskId, userId, "course-1", null, title, null, null, null, status, null);
    }

    private Task createTaskWithDate(String taskId, String userId, String title, Instant dueAt) {
        return new Task(taskId, userId, "course-1", null, title, dueAt, null, null, TaskStatus.TODO, null);
    }

    // In-memory repository for testing
    private static class InMemoryTaskRepository implements TaskRepository {
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
    }
}
