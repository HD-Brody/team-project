package interface_adapter.inbound.web;

import entity.Task;
import entity.TaskStatus;
import interface_adapter.inbound.web.dto.TaskCreationRequest;
import interface_adapter.inbound.web.dto.TaskResponse;
import interface_adapter.inbound.web.dto.TaskUpdateRequest;
import interface_adapter.inbound.web.exception.TaskNotFoundException;
import use_case.dto.TaskCreationCommand;
import use_case.dto.TaskUpdateCommand;
import use_case.port.incoming.TaskEditingUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TaskController focusing on:
 * - DTO conversion (request -> command, entity -> response)
 * - Input validation (null checks, priority range, date format)
 * - Error handling (exception translation)
 * - Default value assignment
 */
class TaskControllerTest {
    
    private TaskController controller;
    private MockTaskEditingUseCase mockUseCase;
    
    @BeforeEach
    void setUp() {
        mockUseCase = new MockTaskEditingUseCase();
        controller = new TaskController(mockUseCase);
    }
    
    // ==================== CREATE TASK TESTS ====================
    
    @Test
    void createTask_shouldSucceed_whenAllFieldsValid() {
        // Given: valid request with all fields
        TaskCreationRequest request = new TaskCreationRequest();
        request.setUserId("user-123");
        request.setCourseId("course-456");
        request.setAssessmentId("assessment-789");
        request.setTitle("Complete Assignment 1");
        request.setDueAt("2025-11-30T23:59:00Z");
        request.setEstimatedEffortMins(120);
        request.setPriority(3);
        request.setStatus(TaskStatus.IN_PROGRESS);
        request.setNotes("Remember to review chapter 5");
        
        // When: creating task
        TaskResponse response = controller.createTask(request);
        
        // Then: response should contain all fields
        assertNotNull(response);
        assertEquals("user-123", response.getUserId());
        assertEquals("course-456", response.getCourseId());
        assertEquals("assessment-789", response.getAssessmentId());
        assertEquals("Complete Assignment 1", response.getTitle());
        assertEquals("2025-11-30T23:59:00Z", response.getDueAt());
        assertEquals(120, response.getEstimatedEffortMins());
        assertEquals(3, response.getPriority());
        assertEquals(TaskStatus.IN_PROGRESS, response.getStatus());
        assertEquals("Remember to review chapter 5", response.getNotes());
    }
    
    @Test
    void createTask_shouldDefaultToTodoStatus_whenStatusIsNull() {
        // Given: request with null status
        TaskCreationRequest request = new TaskCreationRequest();
        request.setUserId("user-123");
        request.setCourseId("course-456");
        request.setTitle("Task without status");
        request.setStatus(null);  // Explicitly null
        
        // When: creating task
        TaskResponse response = controller.createTask(request);
        
        // Then: status should default to TODO
        assertEquals(TaskStatus.TODO, response.getStatus());
    }
    
    @Test
    void createTask_shouldAcceptOptionalFields_asNull() {
        // Given: request with only required fields
        TaskCreationRequest request = new TaskCreationRequest();
        request.setUserId("user-123");
        request.setCourseId("course-456");
        request.setTitle("Minimal Task");
        // Optional fields: assessmentId, dueAt, estimatedEffortMins, priority, notes
        
        // When: creating task
        TaskResponse response = controller.createTask(request);
        
        // Then: should succeed with null optional fields
        assertNotNull(response);
        assertEquals("Minimal Task", response.getTitle());
        assertNull(response.getAssessmentId());
        assertNull(response.getDueAt());
        assertNull(response.getEstimatedEffortMins());
        assertNull(response.getPriority());
        assertNull(response.getNotes());
    }
    
    @Test
    void createTask_shouldThrowException_whenRequestIsNull() {
        // When/Then: null request should throw NullPointerException
        assertThrows(NullPointerException.class, 
            () -> controller.createTask(null));
    }
    
    @Test
    void createTask_shouldThrowException_whenUserIdIsNull() {
        // Given: request with null userId
        TaskCreationRequest request = new TaskCreationRequest();
        request.setUserId(null);
        request.setCourseId("course-456");
        request.setTitle("Task");
        
        // When/Then: should throw NullPointerException
        assertThrows(NullPointerException.class, 
            () -> controller.createTask(request));
    }
    
    @Test
    void createTask_shouldThrowException_whenCourseIdIsNull() {
        // Given: request with null courseId
        TaskCreationRequest request = new TaskCreationRequest();
        request.setUserId("user-123");
        request.setCourseId(null);
        request.setTitle("Task");
        
        // When/Then: should throw NullPointerException
        assertThrows(NullPointerException.class, 
            () -> controller.createTask(request));
    }
    
    @Test
    void createTask_shouldThrowException_whenTitleIsNull() {
        // Given: request with null title
        TaskCreationRequest request = new TaskCreationRequest();
        request.setUserId("user-123");
        request.setCourseId("course-456");
        request.setTitle(null);
        
        // When/Then: should throw NullPointerException
        assertThrows(NullPointerException.class, 
            () -> controller.createTask(request));
    }
    
    @Test
    void createTask_shouldThrowException_whenPriorityIsTooLow() {
        // Given: request with priority < 1
        TaskCreationRequest request = new TaskCreationRequest();
        request.setUserId("user-123");
        request.setCourseId("course-456");
        request.setTitle("Task");
        request.setPriority(0);  // Invalid: too low
        
        // When/Then: should throw IllegalArgumentException
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> controller.createTask(request));
        
        assertTrue(exception.getMessage().contains("Priority must be between 1 and 5"));
        assertTrue(exception.getMessage().contains("0"));
    }
    
    @Test
    void createTask_shouldThrowException_whenPriorityIsTooHigh() {
        // Given: request with priority > 5
        TaskCreationRequest request = new TaskCreationRequest();
        request.setUserId("user-123");
        request.setCourseId("course-456");
        request.setTitle("Task");
        request.setPriority(6);  // Invalid: too high
        
        // When/Then: should throw IllegalArgumentException
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> controller.createTask(request));
        
        assertTrue(exception.getMessage().contains("Priority must be between 1 and 5"));
        assertTrue(exception.getMessage().contains("6"));
    }
    
    @Test
    void createTask_shouldAcceptValidPriorityRange() {
        // Given/When/Then: priorities 1-5 should all be valid
        for (int priority = 1; priority <= 5; priority++) {
            TaskCreationRequest request = new TaskCreationRequest();
            request.setUserId("user-123");
            request.setCourseId("course-456");
            request.setTitle("Task");
            request.setPriority(priority);
            
            assertDoesNotThrow(() -> controller.createTask(request),
                "Priority " + priority + " should be valid");
        }
    }
    
    @Test
    void createTask_shouldThrowException_whenDueDateFormatIsInvalid() {
        // Given: request with invalid date format
        TaskCreationRequest request = new TaskCreationRequest();
        request.setUserId("user-123");
        request.setCourseId("course-456");
        request.setTitle("Task");
        request.setDueAt("2025-11-30");  // Invalid: missing time and zone
        
        // When/Then: should throw IllegalArgumentException
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> controller.createTask(request));
        
        assertTrue(exception.getMessage().contains("Invalid date format"));
        assertTrue(exception.getMessage().contains("ISO-8601"));
    }
    
    @Test
    void createTask_shouldAcceptValidISO8601Formats() {
        // Given/When/Then: various valid ISO-8601 formats
        String[] validFormats = {
            "2025-11-30T23:59:00Z",
            "2025-11-30T23:59:00.123Z",
            "2025-11-30T23:59:00+00:00"
        };
        
        for (String dateStr : validFormats) {
            TaskCreationRequest request = new TaskCreationRequest();
            request.setUserId("user-123");
            request.setCourseId("course-456");
            request.setTitle("Task");
            request.setDueAt(dateStr);
            
            assertDoesNotThrow(() -> controller.createTask(request),
                "Date format " + dateStr + " should be valid");
        }
    }
    
    // ==================== LIST TASKS TESTS ====================
    
    @Test
    void listTasks_shouldReturnAllTasks_whenNoStatusFilter() {
        // Given: mock returns 3 tasks
        mockUseCase.setTasksToReturn(Arrays.asList(
            createTask("task-1", "user-123", TaskStatus.TODO),
            createTask("task-2", "user-123", TaskStatus.IN_PROGRESS),
            createTask("task-3", "user-123", TaskStatus.DONE)
        ));
        
        // When: listing without status filter
        List<TaskResponse> responses = controller.listTasks("user-123", null);
        
        // Then: should return all 3 tasks
        assertEquals(3, responses.size());
    }
    
    @Test
    void listTasks_shouldConvertToResponseDTOs() {
        // Given: mock returns tasks
        mockUseCase.setTasksToReturn(Arrays.asList(
            createTask("task-1", "user-123", TaskStatus.TODO)
        ));
        
        // When: listing tasks
        List<TaskResponse> responses = controller.listTasks("user-123", null);
        
        // Then: should convert to TaskResponse DTOs
        assertEquals(1, responses.size());
        assertInstanceOf(TaskResponse.class, responses.get(0));
        assertEquals("task-1", responses.get(0).getTaskId());
    }
    
    @Test
    void listTasks_shouldAcceptValidStatusParameter() {
        // When: listing with valid status parameter
        controller.listTasks("user-123", "TODO");
        
        // Then: should pass correct enum to use case
        assertEquals(TaskStatus.TODO, mockUseCase.getLastStatusFilter());
    }
    
    @Test
    void listTasks_shouldBeCaseInsensitive_forStatusParameter() {
        // When: listing with lowercase status
        controller.listTasks("user-123", "todo");
        
        // Then: should convert to enum correctly
        assertEquals(TaskStatus.TODO, mockUseCase.getLastStatusFilter());
    }
    
    @Test
    void listTasks_shouldThrowException_whenStatusIsInvalid() {
        // When/Then: invalid status should throw IllegalArgumentException
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> controller.listTasks("user-123", "INVALID_STATUS"));
        
        assertTrue(exception.getMessage().contains("Invalid status"));
        assertTrue(exception.getMessage().contains("INVALID_STATUS"));
    }
    
    @Test
    void listTasks_shouldThrowException_whenUserIdIsNull() {
        // When/Then: null userId should throw NullPointerException
        assertThrows(NullPointerException.class,
            () -> controller.listTasks(null, null));
    }
    
    // ==================== GET TASK TESTS ====================
    
    @Test
    void getTask_shouldReturnTask_whenTaskExists() {
        // Given: mock returns a task
        Task task = createTask("task-1", "user-123", TaskStatus.TODO);
        mockUseCase.setTaskToReturn(task);
        
        // When: getting task
        TaskResponse response = controller.getTask("task-1");
        
        // Then: should return response DTO
        assertNotNull(response);
        assertEquals("task-1", response.getTaskId());
    }
    
    @Test
    void getTask_shouldThrowException_whenTaskNotFound() {
        // Given: mock returns empty optional
        mockUseCase.setTaskToReturn(null);
        
        // When/Then: should throw TaskNotFoundException
        TaskNotFoundException exception = assertThrows(
            TaskNotFoundException.class,
            () -> controller.getTask("nonexistent"));
        
        assertTrue(exception.getMessage().contains("Task not found"));
        assertEquals("nonexistent", exception.getTaskId());
    }
    
    @Test
    void getTask_shouldThrowException_whenTaskIdIsNull() {
        // When/Then: null taskId should throw NullPointerException
        assertThrows(NullPointerException.class,
            () -> controller.getTask(null));
    }
    
    // ==================== UPDATE TASK TESTS ====================
    
    @Test
    void updateTask_shouldSucceed_whenRequestIsValid() {
        // Given: valid update request
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Updated Title");
        request.setStatus(TaskStatus.DONE);
        
        // When/Then: should not throw
        assertDoesNotThrow(() -> controller.updateTask("task-1", request));
    }
    
    @Test
    void updateTask_shouldConvertToCommand_correctly() {
        // Given: update request with date string
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Updated");
        request.setDueAt("2025-12-01T10:00:00Z");
        
        // When: updating task
        controller.updateTask("task-1", request);
        
        // Then: should convert date string to Instant in command
        TaskUpdateCommand command = mockUseCase.getLastUpdateCommand();
        assertNotNull(command);
        assertEquals("Updated", command.getTitle());
        assertEquals(Instant.parse("2025-12-01T10:00:00Z"), command.getDueAt());
    }
    
    @Test
    void updateTask_shouldThrowTaskNotFoundException_whenTaskNotFound() {
        // Given: use case throws "Task not found" IllegalArgumentException
        mockUseCase.setShouldThrowTaskNotFound(true);
        
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Updated");
        
        // When/Then: should convert to TaskNotFoundException
        assertThrows(TaskNotFoundException.class,
            () -> controller.updateTask("nonexistent", request));
    }
    
    @Test
    void updateTask_shouldThrowException_whenTaskIdIsNull() {
        // Given: valid request
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Updated");
        
        // When/Then: null taskId should throw NullPointerException
        assertThrows(NullPointerException.class,
            () -> controller.updateTask(null, request));
    }
    
    @Test
    void updateTask_shouldThrowException_whenRequestIsNull() {
        // When/Then: null request should throw NullPointerException
        assertThrows(NullPointerException.class,
            () -> controller.updateTask("task-1", null));
    }
    
    // ==================== DELETE TASK TESTS ====================
    
    @Test
    void deleteTask_shouldSucceed_whenTaskExists() {
        // When/Then: should not throw
        assertDoesNotThrow(() -> controller.deleteTask("task-1"));
    }
    
    @Test
    void deleteTask_shouldThrowTaskNotFoundException_whenTaskNotFound() {
        // Given: use case throws "Task not found" IllegalArgumentException
        mockUseCase.setShouldThrowTaskNotFoundOnDelete(true);
        
        // When/Then: should convert to TaskNotFoundException
        assertThrows(TaskNotFoundException.class,
            () -> controller.deleteTask("nonexistent"));
    }
    
    @Test
    void deleteTask_shouldThrowException_whenTaskIdIsNull() {
        // When/Then: null taskId should throw NullPointerException
        assertThrows(NullPointerException.class,
            () -> controller.deleteTask(null));
    }
    
    // ==================== HELPER METHODS ====================
    
    private Task createTask(String taskId, String userId, TaskStatus status) {
        return new Task(
            taskId, userId, "course-1", null,
            "Task " + taskId, null, null, null, status, null
        );
    }
    
    // ==================== MOCK USE CASE ====================
    
    /**
     * Mock implementation of TaskEditingUseCase for isolated controller testing.
     * Tracks method calls and allows configuring return values.
     */
    private static class MockTaskEditingUseCase implements TaskEditingUseCase {
        
        private List<Task> tasksToReturn;
        private Task taskToReturn;
        private TaskStatus lastStatusFilter;
        private TaskUpdateCommand lastUpdateCommand;
        private boolean shouldThrowTaskNotFound = false;
        private boolean shouldThrowTaskNotFoundOnDelete = false;
        
        @Override
        public Task createTask(TaskCreationCommand command) {
            // Echo back a task with the command's values
            return new Task(
                "generated-id",
                command.getUserId(),
                command.getCourseId(),
                command.getAssessmentId(),
                command.getTitle(),
                command.getDueAt(),
                command.getEstimatedEffortMins(),
                command.getPriority(),
                command.getStatus(),
                command.getNotes()
            );
        }
        
        @Override
        public List<Task> listTasksForUser(String userId, TaskStatus statusFilter) {
            this.lastStatusFilter = statusFilter;
            return tasksToReturn != null ? tasksToReturn : Arrays.asList();
        }
        
        @Override
        public Optional<Task> getTaskById(String taskId) {
            return Optional.ofNullable(taskToReturn);
        }
        
        @Override
        public void updateTask(TaskUpdateCommand command) {
            this.lastUpdateCommand = command;
            if (shouldThrowTaskNotFound) {
                throw new IllegalArgumentException("Task not found: " + command.getTaskId());
            }
        }
        
        @Override
        public void deleteTask(String taskId) {
            if (shouldThrowTaskNotFoundOnDelete) {
                throw new IllegalArgumentException("Task not found: " + taskId);
            }
        }
        
        // Setter methods for test configuration
        public void setTasksToReturn(List<Task> tasks) {
            this.tasksToReturn = tasks;
        }
        
        public void setTaskToReturn(Task task) {
            this.taskToReturn = task;
        }
        
        public void setShouldThrowTaskNotFound(boolean shouldThrow) {
            this.shouldThrowTaskNotFound = shouldThrow;
        }
        
        public void setShouldThrowTaskNotFoundOnDelete(boolean shouldThrow) {
            this.shouldThrowTaskNotFoundOnDelete = shouldThrow;
        }
        
        // Getter methods for verification
        public TaskStatus getLastStatusFilter() {
            return lastStatusFilter;
        }
        
        public TaskUpdateCommand getLastUpdateCommand() {
            return lastUpdateCommand;
        }
    }
}

