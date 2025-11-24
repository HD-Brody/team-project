package interface_adapter.inbound.web;

import entity.Task;
import entity.TaskStatus;
import interface_adapter.inbound.web.dto.TaskResponse;
import interface_adapter.inbound.web.dto.TaskUpdateRequest;
import interface_adapter.inbound.web.exception.TaskNotFoundException;
import use_case.dto.TaskUpdateCommand;
import use_case.port.incoming.TaskEditingUseCase;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controller for task management operations.
 * Handles web/GUI requests and delegates to use case layer.
 * 
 * SOLID Principles:
 * - Single Responsibility: Only handles request/response translation
 * - Dependency Inversion: Depends on TaskEditingUseCase interface, not implementation
 * - Open/Closed: Can extend with new methods without modifying existing code
 * 
 * Clean Architecture: Interface Adapter layer
 * - Converts web DTOs to use case commands
 * - Converts domain entities to response DTOs
 * - No business logic (that's in TaskEditingService)
 */
public class TaskController {
    private final TaskEditingUseCase taskEditingUseCase;

    public TaskController(TaskEditingUseCase taskEditingUseCase) {
        this.taskEditingUseCase = Objects.requireNonNull(taskEditingUseCase, "taskEditingUseCase");
    }

    /**
     * List all tasks for a user with optional status filter.
     * 
     * @param userId User identifier
     * @param statusParam Optional status filter ("TODO", "IN_PROGRESS", "DONE", "CANCELLED", or null for all)
     * @return List of task responses
     * @throws NullPointerException if userId is null
     */
    public List<TaskResponse> listTasks(String userId, String statusParam) {
        Objects.requireNonNull(userId, "userId");

        // Convert string parameter to enum (null-safe)
        TaskStatus status = null;
        if (statusParam != null && !statusParam.isEmpty()) {
            try {
                status = TaskStatus.valueOf(statusParam.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                    "Invalid status: " + statusParam + ". Must be TODO, IN_PROGRESS, DONE, or CANCELLED");
            }
        }

        // Delegate to use case
        List<Task> tasks = taskEditingUseCase.listTasksForUser(userId, status);

        // Convert domain entities to response DTOs
        return tasks.stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get a single task by ID.
     * 
     * @param taskId Task identifier
     * @return Task response
     * @throws NullPointerException if taskId is null
     * @throws TaskNotFoundException if task doesn't exist
     */
    public TaskResponse getTask(String taskId) {
        Objects.requireNonNull(taskId, "taskId");

        // Delegate to use case
        Task task = taskEditingUseCase.getTaskById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(
                    "Task not found: " + taskId, taskId));

        // Convert domain entity to response DTO
        return new TaskResponse(task);
    }

    /**
     * Update an existing task.
     * 
     * @param taskId Task identifier
     * @param request Update request data (null fields preserve existing values)
     * @throws NullPointerException if taskId or request is null
     * @throws TaskNotFoundException if task doesn't exist
     * @throws IllegalArgumentException if dueAt format is invalid
     */
    public void updateTask(String taskId, TaskUpdateRequest request) {
        Objects.requireNonNull(taskId, "taskId");
        Objects.requireNonNull(request, "request");

        // Convert request DTO to use case command
        TaskUpdateCommand command = new TaskUpdateCommand(
                taskId,
                request.getTitle(),
                parseInstant(request.getDueAt()),
                request.getEstimatedEffortMins(),
                request.getPriority(),
                request.getStatus(),
                request.getNotes()
        );

        // Delegate to use case (will throw IllegalArgumentException if task not found)
        try {
            taskEditingUseCase.updateTask(command);
        } catch (IllegalArgumentException e) {
            // Re-throw as TaskNotFoundException for clearer error handling
            if (e.getMessage().contains("Task not found")) {
                throw new TaskNotFoundException(e.getMessage(), taskId);
            }
            throw e;
        }
    }

    /**
     * Delete a task.
     * 
     * @param taskId Task identifier
     * @throws NullPointerException if taskId is null
     * @throws TaskNotFoundException if task doesn't exist
     */
    public void deleteTask(String taskId) {
        Objects.requireNonNull(taskId, "taskId");

        // Delegate to use case (will throw IllegalArgumentException if task not found)
        try {
            taskEditingUseCase.deleteTask(taskId);
        } catch (IllegalArgumentException e) {
            // Re-throw as TaskNotFoundException for clearer error handling
            if (e.getMessage().contains("Task not found")) {
                throw new TaskNotFoundException(e.getMessage(), taskId);
            }
            throw e;
        }
    }

    /**
     * Helper method to parse ISO-8601 date string to Instant.
     * Returns null if input is null or empty.
     * 
     * @param dateString ISO-8601 formatted date string (e.g., "2025-11-20T23:59:00Z")
     * @return Instant or null
     * @throws IllegalArgumentException if format is invalid
     */
    private Instant parseInstant(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }

        try {
            return Instant.parse(dateString);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid date format: " + dateString + ". Expected ISO-8601 format (e.g., 2025-11-20T23:59:00Z)");
        }
    }
}

