package view.swing;

import interface_adapter.inbound.web.TaskController;
import interface_adapter.inbound.web.dto.TaskResponse;
import interface_adapter.inbound.web.exception.TaskNotFoundException;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Main view for displaying and managing tasks for a course.
 * Shows list of tasks with Edit and Remove buttons.
 */
public class TaskListView extends JFrame {
    
    // Window dimensions - optimized for displaying 4-5 tasks without scrolling
    private static final int WINDOW_WIDTH = 550;
    private static final int WINDOW_HEIGHT = 450;
    
    private final TaskController taskController;
    private final String userId;
    private final String courseCode;
    
    private JPanel taskListPanel;
    private JScrollPane scrollPane;

    public TaskListView(TaskController taskController, String userId, String courseCode) {
        this.taskController = taskController;
        this.userId = userId;
        this.courseCode = courseCode;
        
        initializeUI();
        loadTasks();
    }

    private void initializeUI() {
        // Window setup
        setTitle("Time Till Test - " + courseCode);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);
        
        // Header with course code and Add Task button
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Task list panel (scrollable)
        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setBackground(Color.WHITE);
        
        scrollPane = new JScrollPane(taskListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        // Course code label
        JLabel courseLabel = new JLabel(courseCode);
        courseLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
        
        // Add underline
        courseLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
        
        // Add Task button
        JButton addTaskButton = new JButton("Add");
        addTaskButton.setBackground(new Color(66, 133, 244)); // Blue color
        addTaskButton.setForeground(Color.WHITE);
        addTaskButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        addTaskButton.setFocusPainted(false);
        addTaskButton.setBorderPainted(false);
        addTaskButton.setOpaque(true);
        addTaskButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addTaskButton.setPreferredSize(new Dimension(75, 24));
        
        addTaskButton.addActionListener(e -> openAddTaskDialog());
        
        // Wrapper panel to align button with task list items (adds 20px right margin)
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonWrapper.setBackground(Color.WHITE);
        buttonWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        buttonWrapper.add(addTaskButton);
        
        headerPanel.add(courseLabel, BorderLayout.WEST);
        headerPanel.add(buttonWrapper, BorderLayout.EAST);
        
        return headerPanel;
    }

    private void loadTasks() {
        // Clear existing tasks
        taskListPanel.removeAll();
        
        try {
            // Get all tasks for user (no status filter)
            List<TaskResponse> tasks = taskController.listTasks(userId, null);
            
            if (tasks.isEmpty()) {
                // Show empty state
                JLabel emptyLabel = new JLabel("No tasks yet. Click '+ Add Task' to create one.");
                emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
                emptyLabel.setForeground(Color.GRAY);
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                taskListPanel.add(Box.createVerticalStrut(50));
                taskListPanel.add(emptyLabel);
            } else {
                // Add each task
                for (TaskResponse task : tasks) {
                    TaskItemPanel taskItem = new TaskItemPanel(task, this);
                    taskListPanel.add(taskItem);
                    taskListPanel.add(Box.createVerticalStrut(10));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading tasks: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        
        // Refresh UI
        taskListPanel.revalidate();
        taskListPanel.repaint();
    }

    private void openAddTaskDialog() {
        TaskFormDialog dialog = new TaskFormDialog(this, taskController, userId, courseCode, null);
        dialog.setVisible(true);
        
        // Reload tasks after dialog closes
        loadTasks();
    }

    public void openEditTaskDialog(TaskResponse task) {
        TaskFormDialog dialog = new TaskFormDialog(this, taskController, userId, courseCode, task);
        dialog.setVisible(true);
        
        // Reload tasks after dialog closes
        loadTasks();
    }

    public void deleteTask(String taskId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove this task?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                taskController.deleteTask(taskId);
                loadTasks(); // Refresh list
                JOptionPane.showMessageDialog(this,
                        "Task removed successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (TaskNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                        "Task not found: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error removing task: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}

