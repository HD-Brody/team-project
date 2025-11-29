package infrastructure;

import entity.Task;
import entity.TaskStatus;
import use_case.repository.TaskRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Factory for creating sample/test data for demo purposes.
 * 
 * Separates test data creation from application setup logic.
 * Makes it easy to:
 * - Switch between test data and real data
 * - Configure different test scenarios
 * - Maintain sample data in one place
 * 
 * Usage:
 *   TaskRepository repo = new InMemoryTaskRepository();
 *   SampleDataFactory.loadSampleTasks(repo, "user-123");
 */
public class SampleDataFactory {
    
    /**
     * Loads sample tasks for a CSC236 course into the repository.
     * 
     * @param repository The repository to populate
     * @param userId The user ID to associate tasks with
     */
    public static void loadSampleTasks(TaskRepository repository, String userId) {
        String courseId = "course-csc236";
        
        repository.save(createTask(
            "task-1",
            userId,
            courseId,
            "Term Test III - TEST",
            "2025-10-25T23:59:00Z",
            120,
            5,
            TaskStatus.TODO,
            "Review chapters 8-10"
        ));
        
        repository.save(createTask(
            "task-2",
            userId,
            courseId,
            "Assignment 4 - Graphs",
            "2025-11-15T23:59:00Z",
            180,
            4,
            TaskStatus.IN_PROGRESS,
            "Implement BFS and DFS algorithms"
        ));
        
        repository.save(createTask(
            "task-3",
            userId,
            courseId,
            "Final Exam Prep",
            "2025-12-10T09:00:00Z",
            300,
            5,
            TaskStatus.TODO,
            "Review all lecture notes"
        ));
    }
    
    /**
     * Loads a minimal set of tasks for basic testing.
     * 
     * @param repository The repository to populate
     * @param userId The user ID to associate tasks with
     */
    public static void loadMinimalSampleTasks(TaskRepository repository, String userId) {
        repository.save(createTask(
            "task-1",
            userId,
            "course-test",
            "Sample Task",
            null,
            null,
            null,
            TaskStatus.TODO,
            null
        ));
    }
    
    /**
     * Loads tasks with various statuses for testing filtering.
     * 
     * @param repository The repository to populate
     * @param userId The user ID to associate tasks with
     */
    public static void loadMultiStatusSampleTasks(TaskRepository repository, String userId) {
        String courseId = "course-demo";
        
        repository.save(createTask(
            "task-todo",
            userId,
            courseId,
            "Todo Task",
            tomorrow(),
            60,
            3,
            TaskStatus.TODO,
            "Not started yet"
        ));
        
        repository.save(createTask(
            "task-inprogress",
            userId,
            courseId,
            "In Progress Task",
            tomorrow(),
            90,
            4,
            TaskStatus.IN_PROGRESS,
            "Working on it"
        ));
        
        repository.save(createTask(
            "task-done",
            userId,
            courseId,
            "Completed Task",
            yesterday(),
            120,
            5,
            TaskStatus.DONE,
            "Finished!"
        ));
        
        repository.save(createTask(
            "task-cancelled",
            userId,
            courseId,
            "Cancelled Task",
            yesterday(),
            30,
            2,
            TaskStatus.CANCELLED,
            "No longer needed"
        ));
    }
    
    /**
     * Creates an empty repository (for testing with real data sources).
     * 
     * @param repository The repository to use
     * @param userId Unused (for API consistency)
     */
    public static void loadEmptyData(TaskRepository repository, String userId) {
        // Intentionally empty - repository starts with no data
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Helper method to create a task with all parameters.
     */
    private static Task createTask(
            String taskId,
            String userId,
            String courseId,
            String title,
            String dueDateISO,
            Integer estimatedEffortMins,
            Integer priority,
            TaskStatus status,
            String notes) {
        
        Instant dueAt = dueDateISO != null ? Instant.parse(dueDateISO) : null;
        
        return new Task(
            taskId,
            userId,
            courseId,
            null,  // assessmentId
            title,
            dueAt,
            estimatedEffortMins,
            priority,
            status,
            notes
        );
    }
    
    /**
     * Returns tomorrow's date at 23:59:00 UTC.
     */
    private static String tomorrow() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return tomorrow.atTime(23, 59, 0).toInstant(ZoneOffset.UTC).toString();
    }
    
    /**
     * Returns yesterday's date at 23:59:00 UTC.
     */
    private static String yesterday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return yesterday.atTime(23, 59, 0).toInstant(ZoneOffset.UTC).toString();
    }
}


