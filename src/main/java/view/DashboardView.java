package view;

import interface_adapter.dashboard.DashboardController;
import interface_adapter.dashboard.DashboardState;
import interface_adapter.dashboard.DashboardViewModel;
import interface_adapter.ViewManagerModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DashboardView extends JPanel implements PropertyChangeListener {
    private final String viewName = "dashboard";
    private final DashboardViewModel dashboardViewModel;
    private DashboardController dashboardController;
    private final ViewManagerModel viewManagerModel;

    private final JButton uploadCourseButton;
    private final JButton gradeCalculatorButton;
    private final JButton exportCalendarButton;
    private final JPanel coursesPanel;
    private final String userId = "defaultUser"; // TODO: Get from session

    public DashboardView(DashboardViewModel dashboardViewModel, ViewManagerModel viewManagerModel) {
        this.dashboardViewModel = dashboardViewModel;
        this.dashboardViewModel.addPropertyChangeListener(this);
        this.viewManagerModel = viewManagerModel;

        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 245));

        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        topPanel.setBackground(new Color(240, 240, 245));

        uploadCourseButton = createStyledButton("Upload New Course", new Color(59, 130, 246));
        uploadCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Navigate to syllabus upload view
                dashboardController.navigateToUploadCourse();
            }
        });

        gradeCalculatorButton = createStyledButton("Grade Calculator", new Color(156, 163, 175));
        gradeCalculatorButton.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Grade Calculator - Coming Soon!"));

        exportCalendarButton = createStyledButton("Export to Calendar", new Color(59, 130, 246));
        exportCalendarButton.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Calendar Export - Coming Soon!"));

        topPanel.add(uploadCourseButton);
        topPanel.add(gradeCalculatorButton);
        topPanel.add(exportCalendarButton);

        // Courses panel with scroll
        coursesPanel = new JPanel();
        coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
        coursesPanel.setBackground(new Color(240, 240, 245));

        JScrollPane scrollPane = new JScrollPane(coursesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Reload data when this view becomes visible
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                if (dashboardController != null) {
                    dashboardController.loadDashboard(userId);
                }
            }
        });
        
        uploadCourseButton.addActionListener(e -> {
            viewManagerModel.setState("syllabus upload");
            viewManagerModel.firePropertyChange();
        });
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(180, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        DashboardState state = (DashboardState) evt.getNewValue();
        
        if (state.getError() != null) {
            JOptionPane.showMessageDialog(this, state.getError(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Clear existing courses
        coursesPanel.removeAll();

        // Add each course
        for (DashboardState.CourseDisplayData course : state.getCourses()) {
            coursesPanel.add(createCoursePanel(course));
            coursesPanel.add(Box.createVerticalStrut(20));
        }

        if (state.getCourses().isEmpty()) {
            JLabel emptyLabel = new JLabel("No courses yet. Upload a syllabus to get started!");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            coursesPanel.add(Box.createVerticalStrut(50));
            coursesPanel.add(emptyLabel);
        }

        coursesPanel.revalidate();
        coursesPanel.repaint();
    }

    private JPanel createCoursePanel(DashboardState.CourseDisplayData course) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Course title button
        JButton courseButton = new JButton(course.getCourseCode());
        courseButton.setFont(new Font("Arial", Font.BOLD, 18));
        courseButton.setForeground(new Color(17, 24, 39));
        courseButton.setContentAreaFilled(false);
        courseButton.setBorderPainted(false);
        courseButton.setFocusPainted(false);
        courseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        courseButton.setHorizontalAlignment(SwingConstants.LEFT);
        courseButton.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Course details - Coming Soon!"));

        JLabel courseName = new JLabel(course.getCourseName());
        courseName.setFont(new Font("Arial", Font.PLAIN, 12));
        courseName.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(courseButton, BorderLayout.NORTH);
        titlePanel.add(courseName, BorderLayout.CENTER);

        // Assessments panel
        JPanel assessmentsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        assessmentsPanel.setBackground(Color.WHITE);

        for (DashboardState.AssessmentDisplayData assessment : course.getUpcomingAssessments()) {
            assessmentsPanel.add(createAssessmentCard(assessment));
        }

        // Fill empty slots
        int emptySlots = 4 - course.getUpcomingAssessments().size();
        for (int i = 0; i < emptySlots; i++) {
            assessmentsPanel.add(createEmptyCard());
        }

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(assessmentsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAssessmentCard(DashboardState.AssessmentDisplayData assessment) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(249, 250, 251));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(assessment.getTitle());
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel(assessment.getDueDate());
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(dateLabel);

        return card;
    }

    private JPanel createEmptyCard() {
        JPanel card = new JPanel();
        card.setBackground(new Color(243, 244, 246));
        card.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        return card;
    }

    public String getViewName() {
        return viewName;
    }

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
        // Load data when controller is set
        controller.loadDashboard(userId);
    }
}