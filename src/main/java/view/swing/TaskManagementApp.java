package view.swing;

import entity.Task;
import entity.TaskStatus;
import interface_adapter.inbound.web.TaskController;
import use_case.repository.TaskRepository;
import use_case.service.TaskEditingService;

import javax.swing.*;
import java.time.Instant;
import java.util.*;

/**
 * Main application launcher for Task Management GUI.
 * 
 * This demonstrates how to wire up the Swing GUI with the task management backend.
 * 
 * Current Status:
 * - Uses in-memory repository for testing
 * - TODO: Replace with Leo's SqliteTaskRepositoryAdapter when available
 */
public class TaskManagementApp {
    
    public static void main(String[] args) {
        // Set system look and feel for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default look and feel
        }
        
        SwingUtilities.invokeLater(() -> {
            // Step 1: Create repository (currently in-memory for testing)
            TaskRepository repository = createTestRepository();
            
            // Step 2: Wire up service and controller
            TaskEditingService service = new TaskEditingService(repository);
            TaskController controller = new TaskController(service);
            
            // Step 3: Launch GUI
            TaskListView view = new TaskListView(controller, "user-123", "CSC236");
            view.setVisible(true);
        });
    }
    
    /**
     * Creates an in-memory repository with sample data for testing.
     * 
     * TODO: Replace with Leo's database implementation:
     * - Connection connection = DatabaseConnectionFactory.getConnection();
     * - TaskRepository repository = new SqliteTaskRepositoryAdapter(connection);
     */
    private static TaskRepository createTestRepository() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        
        // Add some sample tasks for testing
        repository.save(new Task(
                "task-1",
                "user-123",
                "course-csc236",
                null,
                "Term Test III - TEST",
                Instant.parse("2025-10-25T23:59:00Z"),
                120,
                5,
                TaskStatus.TODO,
                "Review chapters 8-10"
        ));
        
        repository.save(new Task(
                "task-2",
                "user-123",
                "course-csc236",
                null,
                "Assignment 4 - Graphs",
                Instant.parse("2025-11-15T23:59:00Z"),
                180,
                4,
                TaskStatus.IN_PROGRESS,
                "Implement BFS and DFS algorithms"
        ));
        
        repository.save(new Task(
                "task-3",
                "user-123",
                "course-csc236",
                null,
                "Final Exam Prep",
                Instant.parse("2025-12-10T09:00:00Z"),
                300,
                5,
                TaskStatus.TODO,
                "Review all lecture notes"
        ));
        
        return repository;
    }
    
    /**
     * Simple in-memory repository for testing.
     * Remove when Leo provides SqliteTaskRepositoryAdapter.
     */
    private static class InMemoryTaskRepository implements TaskRepository {
        private final Map<String, Task> tasks = new HashMap<>();
        
        @Override
        public Optional<Task> findById(String taskId) {
            return Optional.ofNullable(tasks.get(taskId));
        }
        
        @Override
        public List<Task> findByUserId(String userId) {
            return new ArrayList<>(tasks.values());
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

