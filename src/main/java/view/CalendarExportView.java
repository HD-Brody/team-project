package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import use_case.service.PreviewType;

/**
 * Swing view for exporting calendar events with course/type filters and a live preview.
 */
public class CalendarExportView extends JPanel implements ActionListener {

    public interface Listener {
        void onPreviewRequested(String courseId, PreviewType previewType);

        void onExportRequested(String courseId, PreviewType previewType);
    }

    private final String viewName = "calendar_export";

    private final JComboBox<String> courseSelector;
    private final JComboBox<String> typeSelector;
    private final JTextArea previewArea;
    private final JButton exportButton;
    private Listener listener;

    public CalendarExportView(List<String> courses) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // Course selector
        courseSelector = new JComboBox<>(courses.toArray(new String[0]));
        courseSelector.setPreferredSize(new Dimension(240, 30));
        courseSelector.setMaximumSize(new Dimension(240, 30));
        courseSelector.setAlignmentX(Component.CENTER_ALIGNMENT);
        courseSelector.addActionListener(this);
        add(courseSelector);

        add(Box.createRigidArea(new Dimension(0, 16)));

        // Type row
        JPanel typeRow = new JPanel();
        typeRow.setLayout(new BoxLayout(typeRow, BoxLayout.X_AXIS));
        typeRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel typeLabel = new JLabel("Type:");
        typeSelector = new JComboBox<>(new String[]{"All", "Assessment", "Scheduled Event"});
        typeSelector.setPreferredSize(new Dimension(180, 30));
        typeSelector.setMaximumSize(new Dimension(180, 30));
        typeSelector.addActionListener(this);
        typeRow.add(typeLabel);
        typeRow.add(Box.createRigidArea(new Dimension(8, 0)));
        typeRow.add(typeSelector);
        add(typeRow);

        add(Box.createRigidArea(new Dimension(0, 16)));

        // Preview label
        JLabel previewLabel = new JLabel("Preview:");
        previewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(previewLabel);

        // Preview area
        previewArea = new JTextArea();
        previewArea.setEditable(false);
        previewArea.setLineWrap(false);
        previewArea.setWrapStyleWord(true);
        previewArea.setBorder(BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY, 1));
        JScrollPane scrollPane = new JScrollPane(previewArea);
        scrollPane.setPreferredSize(new Dimension(320, 140));
        scrollPane.setMaximumSize(new Dimension(320, 140));
        add(scrollPane);

        add(Box.createRigidArea(new Dimension(0, 16)));

        // Export button
        exportButton = new JButton("Export");
        exportButton.setPreferredSize(new Dimension(120, 40));
        exportButton.setMaximumSize(new Dimension(120, 40));
        exportButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exportButton.addActionListener(this);
        add(exportButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (listener == null) {
            return;
        }
        Object source = e.getSource();
        String selectedCourse = (String) courseSelector.getSelectedItem();
        PreviewType previewType = mapPreviewType((String) typeSelector.getSelectedItem());
        if (source == exportButton) {
            listener.onExportRequested(selectedCourse, previewType);
        } else {
            listener.onPreviewRequested(selectedCourse, previewType);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setPreviewLines(List<String> lines) {
        List<String> safeLines = lines == null ? new ArrayList<>() : lines;
        previewArea.setText(String.join(System.lineSeparator(), safeLines));
    }

    public void setCourses(List<String> courses) {
        courseSelector.removeAllItems();
        for (String course : courses) {
            courseSelector.addItem(course);
        }
        courseSelector.revalidate();
        courseSelector.repaint();
    }

    public String getViewName() {
        return viewName;
    }

    private PreviewType mapPreviewType(String label) {
        if ("Assessment".equalsIgnoreCase(label)) {
            return PreviewType.ASSESSMENT;
        }
        if ("Scheduled Event".equalsIgnoreCase(label)) {
            return PreviewType.SCHEDULE_EVENT;
        }
        return PreviewType.ALL;
    }
}
