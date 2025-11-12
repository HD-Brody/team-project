package use_case.dto;

import java.util.Objects;

/**
 * Command carrying data to ingest a syllabus resource.
 */
public final class UploadSyllabusData {
    private final String userId;
    private final String courseId;
    private final String sourceFilePath;

    public UploadSyllabusData(String userId, String courseId, String sourceFilePath) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.courseId = Objects.requireNonNull(courseId, "courseId");
        this.sourceFilePath = Objects.requireNonNull(sourceFilePath, "sourceFilePath");
    }

//    public String getPdf(userId, courseId) {
//        return kk(userID courseID);
//    }

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
