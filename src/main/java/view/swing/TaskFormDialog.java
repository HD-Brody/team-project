package view.swing;

import entity.TaskStatus;
import interface_adapter.inbound.web.TaskController;
import interface_adapter.inbound.web.dto.TaskCreationRequest;
import interface_adapter.inbound.web.dto.TaskResponse;
import interface_adapter.inbound.web.dto.TaskUpdateRequest;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Modal dialog for adding or editing a task.
 * Reuses the same form for both operations.
 */
public class TaskFormDialog extends JDialog {
    private static final Color BUTTON_COLOR = new Color(66, 133, 244);
    
    private final TaskController taskController;
    private final String userId;
    private final String courseCode;
    private final TaskResponse existingTask; // null for add, non-null for edit
    
    private JTextField titleField;
    private JComboBox<TaskStatus> statusComboBox;
    private JTextField dueDateField;
    private JTextField effortField;
    private JTextField priorityField;
    private JTextArea notesArea;

    public TaskFormDialog(JFrame parent, TaskController taskController, String userId, 
                          String courseCode, TaskResponse existingTask) {
        super(parent, existingTask == null ? "Add Task" : "Edit Task", true);
        
        this.taskController = taskController;
        this.userId = userId;
        this.courseCode = courseCode;
        this.existingTask = existingTask;
        
        initializeUI();
        
        if (existingTask != null) {
            populateFields();
        }
    }

    private void initializeUI() {
        setSize(450, 550);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);
        
        // Title field
        mainPanel.add(createFieldPanel("Title", titleField = new JTextField()));
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Status dropdown
        statusComboBox = new JComboBox<>(TaskStatus.values());
        statusComboBox.setPreferredSize(new Dimension(300, 35));
        statusComboBox.setMaximumSize(new Dimension(300, 35));
        mainPanel.add(createFieldPanel("Status", statusComboBox));
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Due date field
        mainPanel.add(createFieldPanel("Due Date", dueDateField = new JTextField()));
        JLabel dateHint = new JLabel("Format: YYYY-MM-DD (e.g., 2025-11-25)");
        dateHint.setFont(new Font("SansSerif", Font.ITALIC, 10));
        dateHint.setForeground(Color.GRAY);
        dateHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(dateHint);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Estimated effort field
        mainPanel.add(createFieldPanel("Estimated Effort (minutes)", effortField = new JTextField()));
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Priority field
        mainPanel.add(createFieldPanel("Priority (1-5)", priorityField = new JTextField()));
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Notes field (text area)
        JLabel notesLabel = new JLabel("Notes");
        notesLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        notesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(notesLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        
        notesArea = new JTextArea(4, 30);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(300, 80));
        notesScroll.setMaximumSize(new Dimension(300, 80));
        notesScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(notesScroll);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Submit button
        JButton submitButton = new JButton(existingTask == null ? "Add Task" : "Update Task");
        submitButton.setBackground(BUTTON_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);
        submitButton.setOpaque(true);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.setPreferredSize(new Dimension(120, 35));
        submitButton.setMaximumSize(new Dimension(120, 35));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> handleSubmit());
        
        mainPanel.add(submitButton);
        
        add(mainPanel);
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        field.setPreferredSize(new Dimension(300, 35));
        field.setMaximumSize(new Dimension(300, 35));
        if (field instanceof JTextField) {
            ((JTextField) field).setFont(new Font("SansSerif", Font.PLAIN, 13));
        }
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        
        return panel;
    }

    private void populateFields() {
        titleField.setText(existingTask.getTitle());
        statusComboBox.setSelectedItem(existingTask.getStatus());
        
        if (existingTask.getDueAt() != null && !existingTask.getDueAt().isEmpty()) {
            try {
                Instant instant = Instant.parse(existingTask.getDueAt());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        .withZone(ZoneId.systemDefault());
                dueDateField.setText(formatter.format(instant));
            } catch (Exception e) {
                dueDateField.setText("");
            }
        }
        
        if (existingTask.getEstimatedEffortMins() != null) {
            effortField.setText(String.valueOf(existingTask.getEstimatedEffortMins()));
        }
        
        if (existingTask.getPriority() != null) {
            priorityField.setText(String.valueOf(existingTask.getPriority()));
        }
        
        if (existingTask.getNotes() != null) {
            notesArea.setText(existingTask.getNotes());
        }
    }

    private void handleSubmit() {
        try {
            // Validate required fields
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Title is required",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse optional fields
            Instant dueAt = parseDueDate(dueDateField.getText().trim());
            Integer effort = parseInteger(effortField.getText().trim());
            Integer priority = parseInteger(priorityField.getText().trim());
            TaskStatus status = (TaskStatus) statusComboBox.getSelectedItem();
            String notes = notesArea.getText().trim();
            if (notes.isEmpty()) notes = null;
            
            // Validate priority range
            if (priority != null && (priority < 1 || priority > 5)) {
                JOptionPane.showMessageDialog(this,
                        "Priority must be between 1 and 5",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (existingTask == null) {
                // Create new task
                createTask(title, dueAt, effort, priority, status, notes);
            } else {
                // Update existing task
                updateTask(title, dueAt, effort, priority, status, notes);
            }
            
            dispose(); // Close dialog
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createTask(String title, Instant dueAt, Integer effort, Integer priority,
                            TaskStatus status, String notes) {
        TaskCreationRequest request = buildCreationRequest(title, dueAt, effort, priority, status, notes);
        TaskResponse createdTask = taskController.createTask(request);
        showSuccessMessage("Task created successfully!\nTask ID: " + createdTask.getTaskId());
    }

    private void updateTask(String title, Instant dueAt, Integer effort, Integer priority,
                            TaskStatus status, String notes) {
        TaskUpdateRequest request = buildUpdateRequest(title, dueAt, effort, priority, status, notes);
        taskController.updateTask(existingTask.getTaskId(), request);
        showSuccessMessage("Task updated successfully");
    }
    
    /**
     * Builds a TaskCreationRequest from form data.
     */
    private TaskCreationRequest buildCreationRequest(String title, Instant dueAt, Integer effort,
                                                     Integer priority, TaskStatus status, String notes) {
        TaskCreationRequest request = new TaskCreationRequest();
        request.setUserId(userId);
        request.setCourseId(courseCode);
        request.setAssessmentId(null); // Manually created tasks have no assessment link
        
        // Set common fields
        String dueDateStr = formatInstantToString(dueAt);
        request.setTitle(title);
        request.setDueAt(dueDateStr);
        request.setEstimatedEffortMins(effort);
        request.setPriority(priority);
        request.setStatus(status);
        request.setNotes(notes);
        
        return request;
    }
    
    /**
     * Builds a TaskUpdateRequest from form data.
     */
    private TaskUpdateRequest buildUpdateRequest(String title, Instant dueAt, Integer effort,
                                                  Integer priority, TaskStatus status, String notes) {
        TaskUpdateRequest request = new TaskUpdateRequest();
        
        // Set common fields (same as creation, but without userId/courseId/assessmentId)
        String dueDateStr = formatInstantToString(dueAt);
        request.setTitle(title);
        request.setDueAt(dueDateStr);
        request.setEstimatedEffortMins(effort);
        request.setPriority(priority);
        request.setStatus(status);
        request.setNotes(notes);
        
        return request;
    }
    
    /**
     * Converts an Instant to ISO-8601 string format, or null if input is null.
     * Extracts common date formatting logic.
     */
    private String formatInstantToString(Instant instant) {
        return instant != null ? instant.toString() : null;
    }
    
    /**
     * Shows a success message dialog.
     * Extracts common dialog display logic.
     */
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private Instant parseDueDate(String dateStr) {
        if (dateStr.isEmpty()) {
            return null;
        }
        
        try {
            // Parse date in format YYYY-MM-DD and convert to Instant at start of day
            // Uses system's default time zone instead of hardcoding UTC
            LocalDate localDate = LocalDate.parse(dateStr);
            return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid date format: '" + dateStr + "'. " +
                "Please use YYYY-MM-DD format (e.g., 2025-11-25)");
        }
    }

    private Integer parseInteger(String str) {
        if (str.isEmpty()) {
            return null;
        }
        
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: '" + str + "'");
        }
    }
}

