package use_case.port.outgoing;

import use_case.dto.SyllabusUploadOutputData;

public interface SyllabusUploadOutputBoundary {
    
    void prepareSuccessView(SyllabusUploadOutputData outputData);
    
    void prepareFailView(String errorMessage);
}