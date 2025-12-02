package use_case.service;

import entity.Assessment;
import entity.AssessmentType;
import entity.TaskStatus;
import use_case.repository.InMemoryAssessmentRepository;
import use_case.dto.TaskCreationCommand;
import use_case.dto.TaskUpdateCommand;
import use_case.repository.AssessmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TaskEditingInteractorTest {
    private AssessmentRepository repository;
    private TaskEditingInteractor service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAssessmentRepository();
        service = new TaskEditingInteractor(repository);
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

        // When: creating task (as assessment)
        Assessment createdAssessment = service.createTask(command);

        // Then: all fields should match
        assertNotNull(createdAssessment);
        assertNotNull(createdAssessment.getAssessmentId(), "assessmentId should be generated");
        assertEquals("course-456", createdAssessment.getCourseId());
        assertEquals("Complete Assignment 1", createdAssessment.getTitle());
        assertNotNull(createdAssessment.getEndsAt(), "endsAt (due date) should be set");
        assertEquals(120L, createdAssessment.getDurationMinutes(), "estimatedEffortMins -> durationMinutes");
        assertEquals(AssessmentType.OTHER, createdAssessment.getType(), "User tasks should be type OTHER");
        assertTrue(createdAssessment.getNotes().contains("[Status: IN_PROGRESS]"), "Status should be in notes");
        assertTrue(createdAssessment.getNotes().contains("Remember to review chapter 5"), "Notes should be preserved");
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
        Assessment createdAssessment = service.createTask(command);

        // Then: required fields present, optional fields null or default
        assertNotNull(createdAssessment);
        assertNotNull(createdAssessment.getAssessmentId());
        assertEquals("course-456", createdAssessment.getCourseId());
        assertEquals("Minimal Task", createdAssessment.getTitle());
        assertEquals(AssessmentType.OTHER, createdAssessment.getType());
        assertTrue(createdAssessment.getNotes().contains("[Status: TODO]"));
        assertNull(createdAssessment.getEndsAt(), "No due date");
        assertNull(createdAssessment.getDurationMinutes());
        assertNull(createdAssessment.getWeight());
    }

    @Test
    void shouldGenerateValidUUID_asAssessmentId() {
        // Given: valid command
        TaskCreationCommand command = new TaskCreationCommand(
                "user-123", "course-456", null, "Task",
                null, null, null, TaskStatus.TODO, null
        );

        // When: creating task
        Assessment createdAssessment = service.createTask(command);

        // Then: assessmentId should be a valid UUID format
        String assessmentId = createdAssessment.getAssessmentId();
        assertNotNull(assessmentId);
        assertDoesNotThrow(() -> UUID.fromString(assessmentId),
                "assessmentId should be a valid UUID");
    }

    @Test
    void shouldGenerateUniqueAssessmentIds() {
        // Given: same command used twice
        TaskCreationCommand command = new TaskCreationCommand(
                "user-123", "course-456", null, "Task",
                null, null, null, TaskStatus.TODO, null
        );

        // When: creating two tasks
        Assessment task1 = service.createTask(command);
        Assessment task2 = service.createTask(command);

        // Then: assessmentIds should be different
        assertNotEquals(task1.getAssessmentId(), task2.getAssessmentId(),
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
        Assessment createdAssessment = service.createTask(command);

        // Then: task should be retrievable from repository
        Optional<Assessment> retrieved = repository.findById(createdAssessment.getAssessmentId());
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

    // ==================== LIST TASKS TESTS ====================

    @Test
    void shouldListAllTasksForCourse() {
        String courseId = "course-123";
        Assessment task1 = createAssessment("task-1", courseId, "Task 1", TaskStatus.TODO);
        Assessment task2 = createAssessment("task-2", courseId, "Task 2", TaskStatus.DONE);
        repository.save(task1);
        repository.save(task2);

        List<Assessment> tasks = service.listTasksForUser(courseId, null);

        assertEquals(2, tasks.size());
    }

    @Test
    void shouldFilterTasksByStatus() {
        String courseId = "course-123";
        repository.save(createAssessment("task-1", courseId, "Todo", TaskStatus.TODO));
        repository.save(createAssessment("task-2", courseId, "Done", TaskStatus.DONE));

        List<Assessment> todoTasks = service.listTasksForUser(courseId, TaskStatus.TODO);

        assertEquals(1, todoTasks.size());
        assertEquals("Todo", todoTasks.get(0).getTitle());
    }

    @Test
    void shouldGetTaskById() {
        Assessment task = createAssessment("task-1", "course-123", "Test", TaskStatus.TODO);
        repository.save(task);

        Optional<Assessment> result = service.getTaskById("task-1");

        assertTrue(result.isPresent());
        assertEquals("Test", result.get().getTitle());
    }

    // ==================== UPDATE TASK TESTS ====================

    @Test
    void shouldUpdateTaskTitle() {
        repository.save(createAssessment("task-1", "course-123", "Old Title", TaskStatus.TODO));

        TaskUpdateCommand command = new TaskUpdateCommand(
                "task-1", "New Title", null, null, null, null, null);
        service.updateTask(command);

        Assessment updated = repository.findById("task-1").get();
        assertEquals("New Title", updated.getTitle());
    }

    @Test
    void shouldUpdateTaskDueDate() {
        Instant originalDate = Instant.parse("2025-11-15T10:00:00Z");
        Instant newDate = Instant.parse("2025-11-20T10:00:00Z");
        repository.save(createAssessmentWithDate("task-1", "course-123", "Test", originalDate));

        TaskUpdateCommand command = new TaskUpdateCommand(
                "task-1", null, newDate, null, null, null, null);
        service.updateTask(command);

        Assessment updated = repository.findById("task-1").get();
        assertNotNull(updated.getEndsAt());
        assertTrue(updated.getEndsAt().contains("2025-11-20"), "Due date should be updated");
    }

    @Test
    void shouldKeepExistingValuesWhenCommandFieldsAreNull() {
        Instant date = Instant.parse("2025-11-15T10:00:00Z");
        repository.save(createAssessmentWithDate("task-1", "course-123", "Original", date));

        TaskUpdateCommand command = new TaskUpdateCommand(
                "task-1", null, null, null, null, null, null);
        service.updateTask(command);

        Assessment updated = repository.findById("task-1").get();
        assertEquals("Original", updated.getTitle());
        assertNotNull(updated.getEndsAt(), "Due date should be preserved");
    }

    @Test
    void shouldUpdateTaskStatus() {
        repository.save(createAssessment("task-1", "course-123", "Test", TaskStatus.TODO));

        TaskUpdateCommand command = new TaskUpdateCommand(
                "task-1", null, null, null, null, TaskStatus.DONE, null);
        service.updateTask(command);

        Assessment updated = repository.findById("task-1").get();
        assertTrue(updated.getNotes().contains("[Status: DONE]"), "Status should be updated in notes");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonexistentTask() {
        TaskUpdateCommand command = new TaskUpdateCommand(
                "nonexistent", "Title", null, null, null, null, null);

        assertThrows(IllegalArgumentException.class, () -> service.updateTask(command));
    }

    // ==================== DELETE TASK TESTS ====================

    @Test
    void shouldDeleteTask() {
        repository.save(createAssessment("task-1", "course-123", "Test", TaskStatus.TODO));

        service.deleteTask("task-1");

        assertFalse(repository.findById("task-1").isPresent());
    }

    // Helper methods
    private Assessment createAssessment(String assessmentId, String courseId, String title, TaskStatus status) {
        String notes = "[Status: " + status.toString() + "]";
        return new Assessment(
                assessmentId,
                courseId,
                title,
                AssessmentType.OTHER,
                -1.0, // -1 means not graded
                null,
                null,
                null,
                0.0, // weight set to 0.0 to avoid null pointer
                "",
                notes
        );
    }

    private Assessment createAssessmentWithDate(String assessmentId, String courseId, String title, Instant dueAt) {
        String endsAt = dueAt.toString();
        String notes = "[Status: TODO]";
        return new Assessment(
                assessmentId,
                courseId,
                title,
                AssessmentType.OTHER,
                -1.0, // -1 means not graded
                null,
                endsAt,
                null,
                0.0, // weight set to 0.0 to avoid null pointer
                "",
                notes
        );
    }
}
