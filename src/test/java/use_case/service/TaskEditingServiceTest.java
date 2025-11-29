package use_case.service;

import entity.Task;
import entity.TaskStatus;
import infrastructure.InMemoryTaskRepository;
import use_case.dto.TaskCreationCommand;
import use_case.dto.TaskUpdateCommand;
import use_case.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TaskEditingServiceTest {
    private TaskRepository repository;
    private TaskEditingService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTaskRepository();
        service = new TaskEditingService(repository);
    }

    // ==================== CREATE TASK TESTS ====================

    @Test
    void shouldCreateTask_withAllFields() {
        // Given: command with all fields populated
        TaskCreationCommand command = new TaskCreationCommand(
                "user-123",
                "course-456",
                "assessment-789",
                "Complete Assignment 1",
                Instant.parse("2025-11-30T23:59:00Z"),
                120,
                3,
                TaskStatus.IN_PROGRESS,
                "Remember to review chapter 5"
        );

        // When: creating task
        Task createdTask = service.createTask(command);

        // Then: all fields should match
        assertNotNull(createdTask);
        assertNotNull(createdTask.getTaskId(), "taskId should be generated");
        assertEquals("user-123", createdTask.getUserId());
        assertEquals("course-456", createdTask.getCourseId());
        assertEquals("assessment-789", createdTask.getAssessmentId());
        assertEquals("Complete Assignment 1", createdTask.getTitle());
        assertEquals(Instant.parse("2025-11-30T23:59:00Z"), createdTask.getDueAt());
        assertEquals(120, createdTask.getEstimatedEffortMins());
        assertEquals(3, createdTask.getPriority());
        assertEquals(TaskStatus.IN_PROGRESS, createdTask.getStatus());
        assertEquals("Remember to review chapter 5", createdTask.getNotes());
    }

    @Test
    void shouldCreateTask_withMinimumRequiredFields() {
        // Given: command with only required fields (userId, courseId, title, status)
        TaskCreationCommand command = new TaskCreationCommand(
                "user-123",
                "course-456",
                null,  // assessmentId optional
                "Minimal Task",
                null,  // dueAt optional
                null,  // estimatedEffortMins optional
                null,  // priority optional
                TaskStatus.TODO,
                null   // notes optional
        );

        // When: creating task
        Task createdTask = service.createTask(command);

        // Then: required fields present, optional fields null
        assertNotNull(createdTask);
        assertNotNull(createdTask.getTaskId());
        assertEquals("user-123", createdTask.getUserId());
        assertEquals("course-456", createdTask.getCourseId());
        assertEquals("Minimal Task", createdTask.getTitle());
        assertEquals(TaskStatus.TODO, createdTask.getStatus());
        assertNull(createdTask.getAssessmentId());
        assertNull(createdTask.getDueAt());
        assertNull(createdTask.getEstimatedEffortMins());
        assertNull(createdTask.getPriority());
        assertNull(createdTask.getNotes());
    }

    @Test
    void shouldGenerateValidUUID_asTaskId() {
        // Given: valid command
        TaskCreationCommand command = new TaskCreationCommand(
                "user-123", "course-456", null, "Task",
                null, null, null, TaskStatus.TODO, null
        );

        // When: creating task
        Task createdTask = service.createTask(command);

        // Then: taskId should be a valid UUID format
        String taskId = createdTask.getTaskId();
        assertNotNull(taskId);
        assertDoesNotThrow(() -> UUID.fromString(taskId),
                "taskId should be a valid UUID");
    }

    @Test
    void shouldGenerateUniqueTaskIds() {
        // Given: same command used twice
        TaskCreationCommand command = new TaskCreationCommand(
                "user-123", "course-456", null, "Task",
                null, null, null, TaskStatus.TODO, null
        );

        // When: creating two tasks
        Task task1 = service.createTask(command);
        Task task2 = service.createTask(command);

        // Then: taskIds should be different
        assertNotEquals(task1.getTaskId(), task2.getTaskId(),
                "Each task should have a unique ID");
    }

    @Test
    void shouldPersistCreatedTask_toRepository() {
        // Given: valid command
        TaskCreationCommand command = new TaskCreationCommand(
                "user-123", "course-456", null, "Persisted Task",
                null, null, null, TaskStatus.TODO, null
        );

        // When: creating task
        Task createdTask = service.createTask(command);

        // Then: task should be retrievable from repository
        Optional<Task> retrieved = repository.findById(createdTask.getTaskId());
        assertTrue(retrieved.isPresent(), "Task should be saved to repository");
        assertEquals("Persisted Task", retrieved.get().getTitle());
    }

    @Test
    void shouldThrowException_whenCommandIsNull() {
        // When/Then: null command should throw NullPointerException
        assertThrows(NullPointerException.class,
                () -> service.createTask(null));
    }

    @Test
    void shouldThrowException_whenUserIdIsNull() {
        // Given: command with null userId
        TaskCreationCommand command = new TaskCreationCommand(
                null, "course-456", null, "Task",
                null, null, null, TaskStatus.TODO, null
        );

        // When/Then: should throw NullPointerException
        assertThrows(NullPointerException.class,
                () -> service.createTask(command));
    }

    @Test
    void shouldThrowException_whenCourseIdIsNull() {
        // Given: command with null courseId
        TaskCreationCommand command = new TaskCreationCommand(
                "user-123", null, null, "Task",
                null, null, null, TaskStatus.TODO, null
        );

        // When/Then: should throw NullPointerException
        assertThrows(NullPointerException.class,
                () -> service.createTask(command));
    }

    @Test
    void shouldThrowException_whenTitleIsNull() {
        // Given: command with null title
        TaskCreationCommand command = new TaskCreationCommand(
                "user-123", "course-456", null, null,
                null, null, null, TaskStatus.TODO, null
        );

        // When/Then: should throw NullPointerException
        assertThrows(NullPointerException.class,
                () -> service.createTask(command));
    }

    @Test
    void shouldThrowException_whenStatusIsNull() {
        // Given: command with null status
        TaskCreationCommand command = new TaskCreationCommand(
                "user-123", "course-456", null, "Task",
                null, null, null, null, null
        );

        // When/Then: should throw NullPointerException
        assertThrows(NullPointerException.class,
                () -> service.createTask(command));
    }

    // ==================== LIST TASKS TESTS ====================

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
}
