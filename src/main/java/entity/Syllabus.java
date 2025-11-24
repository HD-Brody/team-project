package entity;

import java.time.Instant;
import java.util.Objects;

/**
 * Captures the origin of a parsed syllabus document.
 */
public final class Syllabus {
    private final String syllabusId;
    private final String courseId;
    private final String sourceFilePath;

    public Syllabus(String syllabusId, String courseId, String sourceFilePath) {
        this.syllabusId = Objects.requireNonNull(syllabusId, "syllabusId");
        this.courseId = Objects.requireNonNull(courseId, "courseId");
        this.sourceFilePath = Objects.requireNonNull(sourceFilePath, "sourceFilePath");
    }

    public String getSyllabusId() {
        return syllabusId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

}
