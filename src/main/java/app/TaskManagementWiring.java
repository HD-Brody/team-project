package app;

import interface_adapter.inbound.web.TaskController;
import use_case.port.incoming.TaskEditingUseCase;
import use_case.repository.TaskRepository;
import use_case.service.TaskEditingService;

/**
 * Dependency injection wiring for Task Management (Use Case 2).
 * 
 * This class demonstrates Clean Architecture layer connections:
 * 1. Data Access → Repository Interface (Leo's SqliteTaskRepositoryAdapter)
 * 2. Repository → Use Case Service (Rayan's TaskEditingService)
 * 3. Use Case → Controller (Rayan's TaskController)
 * 4. Controller → View/GUI
 * 
 * NOTE: This is a demonstration. Your actual wiring will depend on:
 * - Whether you use a DI framework (Spring, Guice) or manual wiring
 * - How Leo provides the TaskRepository implementation
 * - What GUI framework you choose (JavaFX, Swing, Web)
 */
public class TaskManagementWiring {

    /**
     * Wire up all task management dependencies.
     * 
     * In a real application, this would be called during application startup.
     * 
     * @param taskRepository Leo's SQLite implementation of TaskRepository
     * @return Fully wired TaskController ready for GUI use
     */
    public static TaskController wireTaskManagement(TaskRepository taskRepository) {
        // Layer 1: Use Case Service (business logic)
        TaskEditingUseCase taskEditingUseCase = new TaskEditingService(taskRepository);

        // Layer 2: Interface Adapter (web/GUI controller)
        TaskController taskController = new TaskController(taskEditingUseCase);

        return taskController;
    }

    /**
     * Example usage from GUI code.
     * 
     * This shows how your GUI would interact with the controller.
     */
    public static void exampleUsage(TaskController taskController) {
        // Example: List all TODO tasks for a user
        // List<TaskResponse> tasks = taskController.listTasks("user-123", "TODO");

        // Example: Get a specific task
        // TaskResponse task = taskController.getTask("task-456");

        // Example: Update a task
        // TaskUpdateRequest updateRequest = new TaskUpdateRequest();
        // updateRequest.setTitle("Updated Title");
        // updateRequest.setStatus(TaskStatus.IN_PROGRESS);
        // taskController.updateTask("task-456", updateRequest);

        // Example: Delete a task
        // taskController.deleteTask("task-456");
    }

    /**
     * Full application wiring example.
     * 
     * This would typically be in your Main class or application bootstrap.
     */
    public static void fullApplicationExample() {
        // Step 1: Leo provides the database connection and repository
        // TaskRepository taskRepository = new SqliteTaskRepositoryAdapter(connection);

        // Step 2: Wire up the task management components
        // TaskController taskController = wireTaskManagement(taskRepository);

        // Step 3: Pass controller to your GUI
        // YourGUI gui = new YourGUI(taskController);
        // gui.show();
    }
}

