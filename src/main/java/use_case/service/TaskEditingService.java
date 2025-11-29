package use_case.service;

import entity.Task;
import entity.TaskStatus;
import use_case.dto.TaskCreationCommand;
import use_case.dto.TaskUpdateCommand;
import use_case.port.incoming.TaskEditingUseCase;
import use_case.repository.TaskRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Handles creating, viewing, updating, and deleting user-managed tasks.
 */
public class TaskEditingService implements TaskEditingUseCase {
    private final TaskRepository taskRepository;

    public TaskEditingService(TaskRepository taskRepository) {
        this.taskRepository = Objects.requireNonNull(taskRepository, "taskRepository");
    }

    @Override
    public Task createTask(TaskCreationCommand command) {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(command.getUserId(), "userId");
        Objects.requireNonNull(command.getCourseId(), "courseId");
        Objects.requireNonNull(command.getTitle(), "title");
        
        // Generate new task ID
        String taskId = UUID.randomUUID().toString();
        
        // Create new task entity
        Task newTask = new Task(
                taskId,
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
        
        // Save to repository
        taskRepository.save(newTask);
        
        return newTask;
    }

    @Override
    public List<Task> listTasksForUser(String userId, TaskStatus statusFilter) {
        Objects.requireNonNull(userId, "userId");
        List<Task> tasks = taskRepository.findByUserId(userId);

        if (statusFilter != null) {
            return tasks.stream()
                    .filter(task -> task.getStatus() == statusFilter)
                    .collect(Collectors.toList());
        }
        return tasks;
    }

    @Override
    public Optional<Task> getTaskById(String taskId) {
        Objects.requireNonNull(taskId, "taskId");
        return taskRepository.findById(taskId);
    }

    @Override
    public void updateTask(TaskUpdateCommand command) {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(command.getTaskId(), "taskId");

        Task existingTask = taskRepository.findById(command.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Task not found: " + command.getTaskId()));

        // Immutable entity pattern: create new Task with updates
        Task updatedTask = new Task(
                existingTask.getTaskId(),
                existingTask.getUserId(),
                existingTask.getCourseId(),
                existingTask.getAssessmentId(),
                getOrDefault(command.getTitle(), existingTask.getTitle()),
                getOrDefault(command.getDueAt(), existingTask.getDueAt()),
                getOrDefault(command.getEstimatedEffortMins(), existingTask.getEstimatedEffortMins()),
                getOrDefault(command.getPriority(), existingTask.getPriority()),
                getOrDefault(command.getStatus(), existingTask.getStatus()),
                getOrDefault(command.getNotes(), existingTask.getNotes())
        );

        taskRepository.save(updatedTask);
    }

    @Override
    public void deleteTask(String taskId) {
        Objects.requireNonNull(taskId, "taskId");

        if (!taskRepository.findById(taskId).isPresent()) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        taskRepository.deleteById(taskId);
    }
    
    /**
     * Returns the value if not null, otherwise returns the default value.
     * Helper method to improve readability of update operations.
     *
     * @param value The value to check
     * @param defaultValue The default value to return if value is null
     * @param <T> The type of the value
     * @return value if not null, otherwise defaultValue
     */
    private <T> T getOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
