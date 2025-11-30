package view;

import entity.TaskStatus;
import interface_adapter.ViewManagerModel;
import interface_adapter.grade_calculator.GradeCalculatorController;
import interface_adapter.grade_calculator.GradeCalculatorState;
import interface_adapter.grade_calculator.GradeCalculatorViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeCalculatorView extends JPanel implements PropertyChangeListener {
    private final String viewName = "grade_calculator";
    private final GradeCalculatorViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private GradeCalculatorController controller;
    
    private JLabel courseLabel;
    private JTextField targetPercentField;
    private JPanel assessmentsPanel;
    private JPanel resultsPanel;
    private JScrollPane scrollPane;
    private String currentCourseId;
    private Map<String, JTextField> gradeFields = new HashMap<>();
    private DecimalFormat df = new DecimalFormat("#.##");

    public GradeCalculatorView(GradeCalculatorViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.viewModel.addPropertyChangeListener(this);
        
        initializeUI();
        
        // Reload when view becomes visible
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (controller != null && currentCourseId != null) {
                    controller.loadCourse(currentCourseId);
                }
            }
        });
    }

    private void initializeUI() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content - split between assessments and results
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBackground(Color.WHITE);
        
        // Left: Assessments list
        JPanel leftPanel = createAssessmentsPanel();
        mainPanel.add(leftPanel);
        
        // Right: Results
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Color.WHITE);
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Grade Projections"));
        mainPanel.add(resultsPanel);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        courseLabel = new JLabel("Grade Calculator");
        courseLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        courseLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        // Target grade input
        JLabel targetLabel = new JLabel("Target Grade (%):");
        targetLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        targetPercentField = new JTextField("80.0", 5);
        targetPercentField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        // Calculate button
        JButton calculateButton = new JButton("Calculate");
        calculateButton.setBackground(new Color(66, 133, 244));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        calculateButton.setFocusPainted(false);
        calculateButton.setBorderPainted(false);
        calculateButton.setOpaque(true);
        calculateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        calculateButton.setPreferredSize(new Dimension(100, 32));
        calculateButton.addActionListener(e -> handleCalculate());
        
        // Back button
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
        
        buttonsPanel.add(targetLabel);
        buttonsPanel.add(targetPercentField);
        buttonsPanel.add(calculateButton);
        buttonsPanel.add(backButton);
        
        headerPanel.add(courseLabel, BorderLayout.WEST);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        
        return headerPanel;
    }

    private JPanel createAssessmentsPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createTitledBorder("Assessments & Grades"));
        
        assessmentsPanel = new JPanel();
        assessmentsPanel.setLayout(new BoxLayout(assessmentsPanel, BoxLayout.Y_AXIS));
        assessmentsPanel.setBackground(Color.WHITE);
        
        scrollPane = new JScrollPane(assessmentsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    private void refreshAssessmentsList(List<GradeCalculatorState.AssessmentGradeData> assessments) {
        assessmentsPanel.removeAll();
        gradeFields.clear();
        
        if (assessments.isEmpty()) {
            JLabel emptyLabel = new JLabel("No assessments found for this course.");
            emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            assessmentsPanel.add(Box.createVerticalStrut(50));
            assessmentsPanel.add(emptyLabel);
        } else {
            for (GradeCalculatorState.AssessmentGradeData assessment : assessments) {
                JPanel itemPanel = createAssessmentItem(assessment);
                assessmentsPanel.add(itemPanel);
                assessmentsPanel.add(Box.createVerticalStrut(10));
            }
        }
        
        assessmentsPanel.revalidate();
        assessmentsPanel.repaint();
    }

    private JPanel createAssessmentItem(GradeCalculatorState.AssessmentGradeData assessment) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(new Color(240, 240, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        // Left: Assessment info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(240, 240, 245));
        
        JLabel titleLabel = new JLabel(assessment.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel detailLabel = new JLabel(assessment.getType() + " | Weight: " + df.format(assessment.getWeight() * 100) + "%");
        detailLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        detailLabel.setForeground(Color.DARK_GRAY);
        detailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(detailLabel);
        
        // Right: Grade input
        JPanel gradePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        gradePanel.setBackground(new Color(240, 240, 245));
        
        JLabel gradeLabel = new JLabel("Grade:");
        gradeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        JTextField gradeField = new JTextField(6);
        gradeField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        if (assessment.getGrade() >= 0) {
            gradeField.setText(df.format(assessment.getGrade()));
        } else {
            gradeField.setText("");
        }
        gradeFields.put(assessment.getAssessmentId(), gradeField);
        
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setOpaque(true);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> {
            try {
                String gradeText = gradeField.getText().trim();
                if (gradeText.isEmpty()) {
                    // Empty field means clear the grade
                    controller.updateAssessmentGrade(currentCourseId, assessment.getAssessmentId(), null);
                } else {
                    double grade = Double.parseDouble(gradeText);
                    controller.updateAssessmentGrade(currentCourseId, assessment.getAssessmentId(), grade);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid grade value", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        gradePanel.add(gradeLabel);
        gradePanel.add(gradeField);
        gradePanel.add(saveButton);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(gradePanel, BorderLayout.EAST);
        
        return panel;
    }

    private void handleCalculate() {
        try {
            double targetPercent = Double.parseDouble(targetPercentField.getText().trim());
            if (targetPercent < 0 || targetPercent > 100) {
                JOptionPane.showMessageDialog(this, "Target percent must be between 0 and 100", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            controller.calculateGrades(currentCourseId, targetPercent);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid target percent", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshResults(GradeCalculatorState.CalculationResult result) {
        resultsPanel.removeAll();
        resultsPanel.add(Box.createVerticalStrut(10));
        
        // Current Grade
        JLabel currentLabel = new JLabel("Current Grade: " + df.format(result.getCurrentPercent()) + "%");
        currentLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        currentLabel.setForeground(new Color(59, 130, 246));
        currentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultsPanel.add(currentLabel);
        resultsPanel.add(Box.createVerticalStrut(15));
        
        // Best/Worst Case
        JLabel bestLabel = new JLabel("Best Case: " + df.format(result.getBestCasePercent()) + "%");
        bestLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        bestLabel.setForeground(new Color(40, 167, 69));
        bestLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultsPanel.add(bestLabel);
        resultsPanel.add(Box.createVerticalStrut(5));
        
        JLabel worstLabel = new JLabel("Worst Case: " + df.format(result.getWorstCasePercent()) + "%");
        worstLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        worstLabel.setForeground(new Color(220, 53, 69));
        worstLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultsPanel.add(worstLabel);
        resultsPanel.add(Box.createVerticalStrut(15));
        
        // Required Average
        if (result.getRequiredAverageOnRemaining() != null) {
            JLabel requiredLabel = new JLabel("Required Average on Remaining: " + df.format(result.getRequiredAverageOnRemaining()) + "%");
            requiredLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            requiredLabel.setForeground(new Color(255, 140, 0));
            requiredLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            resultsPanel.add(requiredLabel);
            resultsPanel.add(Box.createVerticalStrut(15));
            
            // Required scores for each assessment
            if (!result.getRequiredScores().isEmpty()) {
                JLabel scoresLabel = new JLabel("Required Scores:");
                scoresLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
                scoresLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                resultsPanel.add(scoresLabel);
                resultsPanel.add(Box.createVerticalStrut(5));
                
                for (GradeCalculatorState.RequiredScoreData score : result.getRequiredScores()) {
                    JLabel scoreLabel = new JLabel("  • " + score.getTitle() + ": " + df.format(score.getRequiredGrade()) + "%");
                    scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    scoreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    resultsPanel.add(scoreLabel);
                    resultsPanel.add(Box.createVerticalStrut(3));
                }
            }
        }
        
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        GradeCalculatorState state = (GradeCalculatorState) evt.getNewValue();
        
        // Update course name
        if (state.getCourseName() != null) {
            courseLabel.setText(state.getCourseName());
        }
        
        // Show error if present
        if (state.getError() != null) {
            JOptionPane.showMessageDialog(this, state.getError(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        // Refresh assessments list
        refreshAssessmentsList(state.getAssessments());
        
        // Refresh results if available
        if (state.getResult() != null) {
            refreshResults(state.getResult());
        } else {
            resultsPanel.removeAll();
            resultsPanel.revalidate();
            resultsPanel.repaint();
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setController(GradeCalculatorController controller) {
        this.controller = controller;
    }

    public void setCourseId(String courseId) {
        this.currentCourseId = courseId;
        if (controller != null) {
            controller.loadCourse(courseId);
        }
    }
}
