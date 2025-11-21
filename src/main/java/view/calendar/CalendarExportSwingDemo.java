package view.calendar;

import entity.SourceKind;
import interface_adapter.outbound.calendar.IcsCalendarRenderer;
import interface_adapter.outbound.calendar.InMemoryCalendarExportGateway;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import use_case.dto.CalendarExportRequest;
import use_case.dto.CalendarExportResponse;
import use_case.dto.ScheduleEventSnapshot;
import use_case.dto.ScheduledTaskSnapshot;
import use_case.service.CalendarExportService;

/**
 * Lightweight Swing demo that exercises the calendar export use case in isolation.
 *
 * <p>Launch this class directly while other teams build their screens. It wires the
 * {@link CalendarExportService} to the in-memory adapters so you can generate an ICS file without
 * persistence or other flows.</p>
 */
public final class CalendarExportSwingDemo {
    private static final String DEFAULT_USER_ID = "user-1";

    private final CalendarExportService calendarExportService;
    private final JTextField userIdField = new JTextField(DEFAULT_USER_ID, 20);
    private final JTextField timezoneField = new JTextField("America/Toronto", 20);
    private final JTextField filenameField = new JTextField("winter-term", 20);
    private final JTextArea previewArea = new JTextArea(8, 32);

    private CalendarExportSwingDemo(CalendarExportService calendarExportService) {
        this.calendarExportService = calendarExportService;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InMemoryCalendarExportGateway gateway = seedDemoData();
            CalendarExportService calendarExportService = new CalendarExportService(
                    gateway, gateway, new IcsCalendarRenderer());
            CalendarExportSwingDemo demo = new CalendarExportSwingDemo(calendarExportService);
            demo.render();
        });
    }

    private static InMemoryCalendarExportGateway seedDemoData() {
        InMemoryCalendarExportGateway gateway = new InMemoryCalendarExportGateway();
        Instant now = Instant.now();
        gateway.addTask(new ScheduledTaskSnapshot(
                "task-essay",
                DEFAULT_USER_ID,
                "CSC207",
                "Design critique essay",
                now.plus(Duration.ofDays(3)),
                180,
                15.0,
                "Online",
                "Submit before midnight"
        ));
        gateway.addTask(new ScheduledTaskSnapshot(
                "task-lab",
                DEFAULT_USER_ID,
                "ECE253",
                "Lab 4 checkoff",
                now.plus(Duration.ofDays(5)).plus(Duration.ofHours(12)),
                90,
                5.0,
                "BA 3175",
                "Bring breadboard kit"
        ));
        gateway.addScheduleEvent(new ScheduleEventSnapshot(
                "event-guest",
                DEFAULT_USER_ID,
                "CSC207 Guest Lecture",
                now.plus(Duration.ofDays(2)).plus(Duration.ofHours(18)),
                now.plus(Duration.ofDays(2)).plus(Duration.ofHours(20)),
                "BA 1160",
                "Attendance optional",
                SourceKind.ASSESSMENT,
                "assessment-guest"
        ));
        return gateway;
    }

    private void render() {
        JFrame frame = new JFrame("Calendar Export (Swing Demo)");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(8, 8));

        frame.add(buildFormPanel(), BorderLayout.NORTH);
        frame.add(buildPreviewPanel(), BorderLayout.CENTER);
        frame.add(buildActionPanel(), BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        addFormRow(form, gbc, 0, "User ID", userIdField);
        addFormRow(form, gbc, 1, "Timezone (IANA)", timezoneField);
        addFormRow(form, gbc, 2, "Filename prefix", filenameField);

        return form;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label,
                            JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label + ":"), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JPanel buildPreviewPanel() {
        previewArea.setEditable(false);
        previewArea.setText(samplePreviewText());
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Preview of seeded events:"), BorderLayout.NORTH);
        panel.add(new JScrollPane(previewArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildActionPanel() {
        JButton exportButton = new JButton("Export calendar to ICS...");
        exportButton.addActionListener(e -> exportCalendar());
        JPanel panel = new JPanel();
        panel.add(exportButton);
        return panel;
    }

    private void exportCalendar() {
        try {
            CalendarExportResponse response = calendarExportService.exportCalendar(
                    new CalendarExportRequest(
                            userIdField.getText().trim(),
                            timezoneField.getText().trim(),
                            List.of(),
                            null,
                            null,
                            List.of(),
                            filenameField.getText().trim()
                    )
            );
            Path targetPath = chooseTargetPath(response.getFilename());
            if (targetPath == null) {
                return;
            }
            Files.write(targetPath, response.getPayload());
            JOptionPane.showMessageDialog(null,
                    "Exported " + response.getEventCount() + " events to\n" + targetPath,
                    "Calendar exported",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null,
                    "Unable to export: " + ex.getMessage(),
                    "Validation error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Failed to save ICS file: " + ex.getMessage(),
                    "Write error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Unexpected error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Path chooseTargetPath(String suggestedFilename) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save calendar as...");
        chooser.setSelectedFile(Path.of(suggestedFilename).toFile());
        int result = chooser.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        Path target = chooser.getSelectedFile().toPath();
        if (!target.toString().toLowerCase(Locale.ROOT).endsWith(".ics")) {
            target = target.resolveSibling(target.getFileName() + ".ics");
        }
        return target;
    }

    private String samplePreviewText() {
        return """
                - CSC207: Design critique essay (15% weight, due ~3 days from launch)
                - ECE253: Lab 4 checkoff (5% weight, due ~5.5 days from launch)
                - CSC207 Guest Lecture (2h block, assessment-linked)

                You can change User ID, timezone, or filename prefix before exporting.
                """;
    }
}
