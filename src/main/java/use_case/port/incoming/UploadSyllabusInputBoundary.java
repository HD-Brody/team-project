package use_case.port.incoming;

import use_case.dto.UploadSyllabusData;

/**
 * Handles ingesting a syllabus file and mapping it into domain entities.
 */
public interface UploadSyllabusInputBoundary {
    void execute(UploadSyllabusData uploadSyllabusData);
}
