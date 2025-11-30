package view.swing;

import interface_adapter.inbound.web.dto.TaskResponse;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Panel representing a single task item in the list.
 * Shows task title, due date, and Edit/Remove buttons.
 */
public class TaskItemPanel extends JPanel {
    private static final Color TASK_BG_COLOR = new Color(230, 230, 230);
    private static final Color EDIT_BUTTON_COLOR = new Color(245, 245, 245);
    private static final Color REMOVE_BUTTON_COLOR = new Color(220, 53, 69);
    
    private final TaskResponse task;
    private final TaskListView parentView;

    public TaskItemPanel(TaskResponse task, TaskListView parentView) {
        this.task = task;
        this.parentView = parentView;
        
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 0));
        setBackground(TASK_BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        setPreferredSize(new Dimension(450, 80));
        
        // Left side: Task info
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.CENTER);
        
        // Right side: Buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.EAST);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(TASK_BG_COLOR);
        
        // Task title
        JLabel titleLabel = new JLabel(task.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Due date
        JLabel dateLabel = new JLabel(formatDueDate(task.getDueAt()));
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        dateLabel.setForeground(Color.DARK_GRAY);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(dateLabel);
        
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(TASK_BG_COLOR);
        
        // Edit button
        JButton editButton = new JButton("Edit");
        editButton.setBackground(EDIT_BUTTON_COLOR);
        editButton.setForeground(Color.DARK_GRAY);
        editButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(false);
        editButton.setOpaque(true);
        editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editButton.setPreferredSize(new Dimension(70, 32));
        editButton.addActionListener(e -> parentView.openEditTaskDialog(task));
        
        // Remove button
        JButton removeButton = new JButton("Remove");
        removeButton.setBackground(REMOVE_BUTTON_COLOR);
        removeButton.setForeground(Color.WHITE);
        removeButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        removeButton.setFocusPainted(false);
        removeButton.setBorderPainted(false);
        removeButton.setOpaque(true);
        removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeButton.setPreferredSize(new Dimension(85, 32));
        removeButton.addActionListener(e -> parentView.deleteTask(task.getTaskId()));
        
        panel.add(editButton);
        panel.add(removeButton);
        
        return panel;
    }

    private String formatDueDate(String dueAtString) {
        if (dueAtString == null || dueAtString.isEmpty()) {
            return "No due date";
        }
        
        try {
            Instant instant = Instant.parse(dueAtString);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                    .withZone(ZoneId.systemDefault());
            return formatter.format(instant);
        } catch (Exception e) {
            return dueAtString; // Return as-is if parsing fails
        }
    }
}

