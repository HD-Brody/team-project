package use_case.dto;

import java.util.Objects;

/**
 * Command carrying data to ingest a syllabus resource.
 */
public final class UploadSyllabusCommand {
    private final String userId;
    private final String courseId;
    private final String sourceFilePath;

    public UploadSyllabusCommand(String userId, String courseId, String sourceFilePath) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.courseId = Objects.requireNonNull(courseId, "courseId");
        this.sourceFilePath = Objects.requireNonNull(sourceFilePath, "sourceFilePath");
    }

    public String getUserId() {
        return userId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }
}
