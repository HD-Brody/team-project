package view;

import entity.TaskStatus;
import interface_adapter.ViewManagerModel;
import interface_adapter.task_list.TaskListController;
import interface_adapter.task_list.TaskListState;
import interface_adapter.task_list.TaskListViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Main view for displaying and managing tasks/assessments for a course.
 * Refactored to follow Clean Architecture principles.
 */
public class TaskListView extends JPanel implements PropertyChangeListener {
    private final String viewName = "task_list";
    private final TaskListViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private TaskListController controller;
    
    private JLabel courseLabel;
    private JPanel taskListPanel;
    private JScrollPane scrollPane;
    private String currentCourseId;

    public TaskListView(TaskListViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.viewModel.addPropertyChangeListener(this);
        
        initializeUI();
        
        // Reload tasks when view becomes visible
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (controller != null && currentCourseId != null) {
                    controller.loadTasks(currentCourseId);
                }
            }
        });
    }

    private void initializeUI() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        setBackground(Color.WHITE);
        
        // Header with course name and buttons
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Task list panel (scrollable)
        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setBackground(Color.WHITE);
        
        scrollPane = new JScrollPane(taskListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        // Course name label
        courseLabel = new JLabel("Tasks");
        courseLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        courseLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        // Add Task button
        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.setBackground(new Color(66, 133, 244));
        addTaskButton.setForeground(Color.WHITE);
        addTaskButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        addTaskButton.setFocusPainted(false);
        addTaskButton.setBorderPainted(false);
        addTaskButton.setOpaque(true);
        addTaskButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addTaskButton.setPreferredSize(new Dimension(100, 32));
        addTaskButton.addActionListener(e -> openAddTaskDialog());
        
        // Back to Dashboard button
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(156, 163, 175));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setOpaque(true);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(150, 32));
        backButton.addActionListener(e -> {
            viewManagerModel.setState("dashboard");
            viewManagerModel.firePropertyChange();
        });
        
        buttonsPanel.add(addTaskButton);
        buttonsPanel.add(backButton);
        
        headerPanel.add(courseLabel, BorderLayout.WEST);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        
        return headerPanel;
    }

    private void refreshTaskList(List<TaskListState.TaskData> tasks) {
        taskListPanel.removeAll();
        
        if (tasks.isEmpty()) {
            JLabel emptyLabel = new JLabel("No tasks yet. Click 'Add Task' to create one.");
            emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            taskListPanel.add(Box.createVerticalStrut(50));
            taskListPanel.add(emptyLabel);
        } else {
            for (TaskListState.TaskData task : tasks) {
                JPanel taskItem = createTaskItemPanel(task);
                taskListPanel.add(taskItem);
                taskListPanel.add(Box.createVerticalStrut(10));
            }
        }
        
        taskListPanel.revalidate();
        taskListPanel.repaint();
    }

    private JPanel createTaskItemPanel(TaskListState.TaskData task) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(new Color(230, 230, 230));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Left: Task info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(230, 230, 230));
        
        JLabel titleLabel = new JLabel(task.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel dateLabel = new JLabel("Due: " + task.getDueDate() + " | Status: " + task.getStatus());
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        dateLabel.setForeground(Color.DARK_GRAY);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(dateLabel);
        
        // Right: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(230, 230, 230));
        
        JButton editButton = new JButton("Edit");
        editButton.setBackground(new Color(40, 167, 69));
        editButton.setForeground(Color.WHITE);
        editButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(false);
        editButton.setOpaque(true);
        editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editButton.setPreferredSize(new Dimension(85, 32));
        editButton.addActionListener(e -> openEditTaskDialog(task));
        
        JButton removeButton = new JButton("Remove");
        removeButton.setBackground(new Color(220, 53, 69));
        removeButton.setForeground(Color.WHITE);
        removeButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        removeButton.setFocusPainted(false);
        removeButton.setBorderPainted(false);
        removeButton.setOpaque(true);
        removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeButton.setPreferredSize(new Dimension(85, 32));
        removeButton.addActionListener(e -> deleteTask(task.getAssessmentId()));
        
        buttonPanel.add(editButton);
        buttonPanel.add(removeButton);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }

    private void openAddTaskDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Task", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField titleField = new JTextField(30);
        JTextField dueDateField = new JTextField(30);
        JComboBox<TaskStatus> statusBox = new JComboBox<>(TaskStatus.values());
        JTextArea notesArea = new JTextArea(4, 30);
        notesArea.setLineWrap(true);
        
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(new JLabel("Due Date (YYYY-MM-DD):"));
        formPanel.add(dueDateField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusBox);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(new JLabel("Notes:"));
        formPanel.add(new JScrollPane(notesArea));
        formPanel.add(Box.createVerticalStrut(20));
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Title is required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            controller.createTask(
                title,
                dueDateField.getText().trim(),
                null,
                (TaskStatus) statusBox.getSelectedItem(),
                notesArea.getText().trim()
            );
            
            dialog.dispose();
        });
        
        formPanel.add(saveButton);
        
        dialog.add(formPanel);
        dialog.setVisible(true);
    }

    private void deleteTask(String assessmentId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove this task?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteTask(assessmentId);
        }
    }

    private void openEditTaskDialog(TaskListState.TaskData task) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Task", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField titleField = new JTextField(task.getTitle(), 30);
        
        // Parse the formatted date back to YYYY-MM-DD
        String dateValue = "";
        if (task.getDueDate() != null && !task.getDueDate().equals("No due date")) {
            try {
                // Try to extract the date from formatted string
                dateValue = task.getDueDate(); // Store as-is for now
            } catch (Exception e) {
                dateValue = "";
            }
        }
        JTextField dueDateField = new JTextField(dateValue, 30);
        
        JComboBox<TaskStatus> statusBox = new JComboBox<>(TaskStatus.values());
        try {
            statusBox.setSelectedItem(TaskStatus.valueOf(task.getStatus()));
        } catch (Exception e) {
            statusBox.setSelectedItem(TaskStatus.TODO);
        }
        
        JTextArea notesArea = new JTextArea(task.getNotes(), 4, 30);
        notesArea.setLineWrap(true);
        
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(new JLabel("Due Date (YYYY-MM-DD):"));
        formPanel.add(dueDateField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusBox);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(new JLabel("Notes:"));
        formPanel.add(new JScrollPane(notesArea));
        formPanel.add(Box.createVerticalStrut(20));
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Title is required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            controller.updateTask(
                task.getAssessmentId(),
                title,
                dueDateField.getText().trim(),
                null,
                (TaskStatus) statusBox.getSelectedItem(),
                notesArea.getText().trim()
            );
            
            dialog.dispose();
        });
        
        formPanel.add(saveButton);
        
        dialog.add(formPanel);
        dialog.setVisible(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        TaskListState state = (TaskListState) evt.getNewValue();
        
        // Update course name
        courseLabel.setText(state.getCourseName());
        
        // Show error if present
        if (state.getError() != null) {
            JOptionPane.showMessageDialog(this,
                    state.getError(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        
        // Refresh task list
        refreshTaskList(state.getTasks());
    }

    public String getViewName() {
        return viewName;
    }

    public void setController(TaskListController controller) {
        this.controller = controller;
    }

    public void setCourseId(String courseId) {
        this.currentCourseId = courseId;
        if (controller != null) {
            controller.loadTasks(courseId);
        }
    }
}
