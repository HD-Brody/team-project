package view.swing;

import infrastructure.InMemoryTaskRepository;
import infrastructure.SampleDataFactory;
import interface_adapter.inbound.web.TaskController;
import use_case.repository.TaskRepository;
import use_case.service.TaskEditingService;

import javax.swing.*;

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
        
        // Load sample data using the factory
        // Switch to SampleDataFactory.loadEmptyData() when ready for real data
        SampleDataFactory.loadSampleTasks(repository, "user-123");
        
        return repository;
    }
}

