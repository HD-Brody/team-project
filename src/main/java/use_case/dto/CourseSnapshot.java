package use_case.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Lightweight representation of stored course data for restoration flows.
 */
public final class CourseSnapshot {
    private final String userId;
    private final List<String> courseIds;
    private final List<String> taskIds;

    public CourseSnapshot(String userId, List<String> courseIds, List<String> taskIds) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.courseIds = Collections.unmodifiableList(
                Objects.requireNonNull(courseIds, "courseIds"));
        this.taskIds = Collections.unmodifiableList(
                Objects.requireNonNull(taskIds, "taskIds"));
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getCourseIds() {
        return courseIds;
    }

    public List<String> getTaskIds() {
        return taskIds;
    }
}
