package interface_adapter.syllabus_upload;

import use_case.port.incoming.UploadSyllabusInputBoundary;
import use_case.dto.SyllabusUploadInputData; // Add this import for UploadSyllabusData

public class SyllabusUploadController {
    private final UploadSyllabusInputBoundary pdfUploadUseCaseInteractor; // Replace with your actual interactor class

    public SyllabusUploadController(UploadSyllabusInputBoundary pdfUploadUseCaseInteractor) {
        this.pdfUploadUseCaseInteractor = pdfUploadUseCaseInteractor;
    }

    public void extractAssessments(String userId, String filePath) {
        final SyllabusUploadInputData data = new SyllabusUploadInputData(userId, filePath);

        pdfUploadUseCaseInteractor.execute(data); 
    }
}