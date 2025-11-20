package entity;

import java.util.Objects;

/**
 * Course metadata that anchors syllabus, assessments, and tasks.
 */
public final class Course {
    private final String courseId;
    private final String userId;
    private final String code;
    private final String name;
    private final String term;
    private final String instructor;

    public Course(String courseId, String userId, String code, String name, String term,
                  String instructor) {
        this.courseId = Objects.requireNonNull(courseId, "courseId");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.code = Objects.requireNonNull(code, "code");
        this.name = Objects.requireNonNull(name, "name");
        this.term = Objects.requireNonNull(term, "term");
        this.instructor = Objects.requireNonNull(instructor, "instructor");
    }

    public String getCourseId() {
        return courseId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getTerm() {
        return term;
    }

    public String getInstructor() {
        return instructor;
    }
}
