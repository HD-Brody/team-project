package interface_adapter.calendar_export;

import use_case.dto.CalendarExportResponse;
import use_case.port.outgoing.CalendarExportOutputPort;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CalendarExportPresenter implements CalendarExportOutputPort {
    private final CalendarExportViewModel viewModel;

    public CalendarExportPresenter(CalendarExportViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentExport(CalendarExportResponse response) {
        // Save the file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(response.getFilename()));
        int result = fileChooser.showSaveDialog(null);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(response.getPayload());
                JOptionPane.showMessageDialog(null,
                    "Calendar exported successfully!\n" + response.getEventCount() + " events exported.",
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                presentError("Failed to save file: " + e.getMessage());
            }
        }
    }

    @Override
    public void presentError(String errorMessage) {
        CalendarExportState state = viewModel.getState();
        state.setError(errorMessage);
        viewModel.setState(state);
        viewModel.firePropertyChange();
        
        JOptionPane.showMessageDialog(null,
            errorMessage,
            "Export Error",
            JOptionPane.ERROR_MESSAGE);
    }

    public void presentPreview(List<String> previewLines) {
        CalendarExportState state = viewModel.getState();
        state.setPreviewLines(previewLines);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }
}
