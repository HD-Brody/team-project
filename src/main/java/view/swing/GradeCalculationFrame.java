package view.swing;

import interface_adapter.grade.GradeCalculationController;
import interface_adapter.grade.GradeCalculationState;
import interface_adapter.grade.GradeCalculationViewModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

/**
 * Swing window for grade target calculations.
 */
public class GradeCalculationFrame extends JFrame implements PropertyChangeListener {
    private final GradeCalculationViewModel viewModel;
    private final GradeCalculationController controller;
    private final JTextField userField = new JTextField(15);
    private final JComboBox<CourseOptionItem> courseComboBox = new JComboBox<>();
    private final JTextField targetField = new JTextField(6);
    private final JTextField projectedField = new JTextField(8);
    private final JLabel errorLabel = new JLabel(" ");
    private final RequiredScoresTableModel tableModel = new RequiredScoresTableModel();
    private boolean updatingState;
    private String lastErrorMessage;
    private final DecimalFormat numberFormat = new DecimalFormat("#0.00");

    public GradeCalculationFrame(GradeCalculationViewModel viewModel, GradeCalculationController controller) {
        super("Grade Target Calculator");
        this.viewModel = viewModel;
        this.controller = controller;
        this.viewModel.addPropertyChangeListener(this);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        setPreferredSize(new Dimension(800, 500));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        controller.loadCourses(viewModel.getState().getUserId());
    }

    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        int col = 0;

        gbc.gridx = col++;
        panel.add(new JLabel("User ID:"), gbc);

        gbc.gridx = col++;
        panel.add(userField, gbc);

        gbc.gridx = col++;
        panel.add(new JLabel("Course:"), gbc);

        gbc.gridx = col++;
        courseComboBox.setPreferredSize(new Dimension(200, 24));
        courseComboBox.addActionListener(e -> handleCourseChanged());
        panel.add(courseComboBox, gbc);

        gbc.gridx = col++;
        panel.add(new JLabel("Target %:"), gbc);

        gbc.gridx = col++;
        targetField.setText(numberFormat.format(viewModel.getState().getTargetPercent()));
        panel.add(targetField, gbc);

        gbc.gridx = col++;
        JButton calculateButton = new JButton(new AbstractAction("Calculate Targets") {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                handleCalculate();
            }
        });
        panel.add(calculateButton, gbc);

        return panel;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(760, 320));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        panel.add(new JLabel("Projected %:"), gbc);

        projectedField.setEditable(false);
        projectedField.setBorder(BorderFactory.createLoweredBevelBorder());
        gbc.gridx = 1;
        panel.add(projectedField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        errorLabel.setForeground(java.awt.Color.RED);
        panel.add(errorLabel, gbc);

        return panel;
    }

    private void handleCalculate() {
        CourseOptionItem selected = (CourseOptionItem) courseComboBox.getSelectedItem();
        if (selected == null) {
            controller.presentInputError("Select a course before calculating.");
            return;
        }

        String userId = userField.getText().trim();
        Double targetPercent = parseTargetPercent();
        if (targetPercent == null) {
            return;
        }

        controller.calculateTargets(userId, selected.option.getCourseId(), targetPercent);
    }

    private void handleCourseChanged() {
        if (updatingState) {
            return;
        }
        CourseOptionItem selected = (CourseOptionItem) courseComboBox.getSelectedItem();
        if (selected == null) {
            return;
        }
        String userId = userField.getText().trim();
        Double targetPercent = parseTargetPercent();
        if (targetPercent == null) {
            targetPercent = viewModel.getState().getTargetPercent();
            targetField.setText(numberFormat.format(targetPercent));
        }
        controller.calculateTargets(userId, selected.option.getCourseId(), targetPercent);
    }

    private Double parseTargetPercent() {
        String text = targetField.getText().trim();
        try {
            double value = Double.parseDouble(text);
            if (value < 0 || value > 100) {
                controller.presentInputError("Target percent must be between 0 and 100.");
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            controller.presentInputError("Enter a numeric target percent.");
            return null;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) {
            return;
        }
        GradeCalculationState state = (GradeCalculationState) evt.getNewValue();
        applyState(state);
    }

    private void applyState(GradeCalculationState state) {
        updatingState = true;

        userField.setText(state.getUserId() != null ? state.getUserId() : "");

        List<CourseOptionItem> items = new ArrayList<>();
        for (GradeCalculationState.CourseOption option : state.getCourses()) {
            items.add(new CourseOptionItem(option));
        }
        CourseOptionItem selectedItem = null;
        if (!items.isEmpty()) {
            for (CourseOptionItem item : items) {
                if (item.option.getCourseId().equals(state.getCourseId())) {
                    selectedItem = item;
                    break;
                }
            }
            if (selectedItem == null) {
                selectedItem = items.get(0);
            }
        }
        courseComboBox.setModel(new DefaultComboBoxModel<>(items.toArray(new CourseOptionItem[0])));
        if (selectedItem != null) {
            courseComboBox.setSelectedItem(selectedItem);
        }

        targetField.setText(numberFormat.format(state.getTargetPercent()));

        if (state.getProjectedPercent() != null) {
            projectedField.setText(numberFormat.format(state.getProjectedPercent()));
        } else {
            projectedField.setText("");
        }

        tableModel.setRows(state.getRequiredScores());

        errorLabel.setText(state.getErrorMessage() != null ? state.getErrorMessage() : " ");
        if (state.getErrorMessage() != null && !state.getErrorMessage().equals(lastErrorMessage)) {
            lastErrorMessage = state.getErrorMessage();
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, state.getErrorMessage(), "Input Error",
                            JOptionPane.ERROR_MESSAGE));
        } else if (state.getErrorMessage() == null) {
            lastErrorMessage = null;
        }

        updatingState = false;

        if (state.getProjectedPercent() == null && state.getErrorMessage() == null
                && courseComboBox.getSelectedItem() != null) {
            handleCourseChanged();
        }
    }

    private static final class CourseOptionItem {
        private final GradeCalculationState.CourseOption option;

        private CourseOptionItem(GradeCalculationState.CourseOption option) {
            this.option = option;
        }

        @Override
        public String toString() {
            return option.getDisplayName();
        }
    }

    private static final class RequiredScoresTableModel extends AbstractTableModel {
        private static final String[] COLUMNS = {
                "Assessment", "Type", "Weight (%)", "Current Grade", "Required Grade"
        };

        private final List<GradeCalculationState.RequiredScoreRow> rows = new ArrayList<>();
        private final DecimalFormat cellFormat = new DecimalFormat("#0.00");

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            GradeCalculationState.RequiredScoreRow row = rows.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return row.getAssessmentTitle();
                case 1:
                    return row.getAssessmentType();
                case 2:
                    return cellFormat.format(row.getWeightPercent());
                case 3:
                    return row.getCurrentGrade() == null ? "" : cellFormat.format(row.getCurrentGrade());
                case 4:
                    return row.getRequiredGrade() == null ? "" : cellFormat.format(row.getRequiredGrade());
                default:
                    return "";
            }
        }

        public void setRows(List<GradeCalculationState.RequiredScoreRow> newRows) {
            rows.clear();
            if (newRows != null) {
                rows.addAll(newRows);
            }
            fireTableDataChanged();
        }
    }
}
