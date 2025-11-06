package application.port.incoming;

import application.dto.UploadSyllabusCommand;

/**
 * Handles ingesting a syllabus file and mapping it into domain entities.
 */
public interface UploadSyllabusUseCase {
    void uploadSyllabus(UploadSyllabusCommand command);
}
