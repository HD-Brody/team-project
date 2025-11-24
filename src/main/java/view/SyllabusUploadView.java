package view;

import interface_adapter.syllabus_upload.SyllabusUploadController;
import interface_adapter.syllabus_upload.SyllabusUploadState;
import interface_adapter.syllabus_upload.SyllabusUploadViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for the Syllabus Upload Use Case.
 */
public class SyllabusUploadView extends JPanel implements ActionListener, PropertyChangeListener {
    private final String viewName = "syllabus upload";
    
    private final SyllabusUploadViewModel syllabusUploadViewModel;
    private SyllabusUploadController syllabusUploadController;
    
    private final JTextField filePathField = new JTextField(30);
    private final JButton selectButton;
    private final JButton extractButton;
    private final JLabel errorLabel;
    
    private final String userId = "defaultUser"; // TODO: Get from session/login

    public SyllabusUploadView(SyllabusUploadViewModel syllabusUploadViewModel) {
        this.syllabusUploadViewModel = syllabusUploadViewModel;
        this.syllabusUploadViewModel.addPropertyChangeListener(this);

        // Title
        final JLabel title = new JLabel("Upload Syllabus PDF");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        // File path display
        filePathField.setEditable(false);
        filePathField.setMaximumSize(new Dimension(500, 30));
        filePathField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Select PDF button
        selectButton = new JButton("Select PDF File");
        selectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                        "PDF files", "pdf"));
                int returnValue = fileChooser.showOpenDialog(SyllabusUploadView.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    filePathField.setText(filePath);
                    
                    // Update state
                    final SyllabusUploadState currentState = syllabusUploadViewModel.getState();
                    currentState.setFilePath(filePath);
                    syllabusUploadViewModel.setState(currentState);
                }
            }
        });

        // Extract button
        extractButton = new JButton("Extract Assessments");
        extractButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        extractButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final SyllabusUploadState currentState = syllabusUploadViewModel.getState();
                String filePath = currentState.getFilePath();
                
                if (filePath == null || filePath.isEmpty()) {
                    JOptionPane.showMessageDialog(SyllabusUploadView.this, 
                            "Please select a PDF file first",
                            "No File Selected",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Set processing state
                currentState.setProcessing(true);
                syllabusUploadViewModel.setState(currentState);
                
                // Execute the use case
                syllabusUploadController.extractAssessments(userId, filePath);
            }
        });

        // Error label
        errorLabel = new JLabel("");
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setForeground(Color.RED);

        // Layout
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        this.add(Box.createVerticalStrut(30));
        this.add(title);
        this.add(Box.createVerticalStrut(20));
        this.add(filePathField);
        this.add(Box.createVerticalStrut(10));
        this.add(selectButton);
        this.add(Box.createVerticalStrut(10));
        this.add(extractButton);
        this.add(Box.createVerticalStrut(10));
        this.add(errorLabel);
        this.add(Box.createVerticalStrut(30));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        // Handle other action events if needed
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final SyllabusUploadState state = (SyllabusUploadState) evt.getNewValue();
        
        // Handle error display
        if (state.getError() != null) {
            errorLabel.setText(state.getError());
            JOptionPane.showMessageDialog(this, 
                    state.getError(),
                    "Upload Failed",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            errorLabel.setText("");
        }
        
        // Handle processing state (disable buttons while processing)
        if (state.isProcessing()) {
            selectButton.setEnabled(false);
            extractButton.setEnabled(false);
            extractButton.setText("Processing...");
        } else {
            selectButton.setEnabled(true);
            extractButton.setEnabled(true);
            extractButton.setText("Extract Assessments");
            
            // If no error and not processing, it means success
            if (state.getError() == null) {
                JOptionPane.showMessageDialog(this,
                        "Syllabus uploaded successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                // Clear the file path for next upload
                filePathField.setText("");
                final SyllabusUploadState currentState = syllabusUploadViewModel.getState();
                currentState.setFilePath("");
                syllabusUploadViewModel.setState(currentState);
            }
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setSyllabusUploadController(SyllabusUploadController controller) {
        this.syllabusUploadController = controller;
    }
}