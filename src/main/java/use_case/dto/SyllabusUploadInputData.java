package use_case.dto;

import java.util.Objects;

/**
 * Command carrying data to ingest a syllabus resource.
 */
public final class SyllabusUploadInputData {
    private final String userId;
    private final String sourceFilePath;

    public SyllabusUploadInputData(String userId, String sourceFilePath) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.sourceFilePath = Objects.requireNonNull(sourceFilePath, "sourceFilePath");
    }

    public String getUserId() {
        return userId;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }
}
