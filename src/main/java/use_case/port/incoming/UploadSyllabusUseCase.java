package use_case.port.incoming;

import use_case.dto.UploadSyllabusCommand;

/**
 * Handles ingesting a syllabus file and mapping it into domain entities.
 */
public interface UploadSyllabusUseCase {
    void uploadSyllabus(UploadSyllabusCommand command);
}
