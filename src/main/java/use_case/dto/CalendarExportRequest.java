package use_case.dto;

import java.util.Objects;

/**
 * Parameters required to generate a calendar view of coursework.
 */
public final class CalendarExportRequest {
    private final String userId;
    private final String courseId;
    private final boolean includeTasks;
    private final String destination;

    public CalendarExportRequest(String userId, String courseId, boolean includeTasks,
                                 String destination) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.courseId = Objects.requireNonNull(courseId, "courseId");
        this.includeTasks = includeTasks;
        this.destination = Objects.requireNonNull(destination, "destination");
    }

    public String getUserId() {
        return userId;
    }

    public String getCourseId() {
        return courseId;
    }

    public boolean isIncludeTasks() {
        return includeTasks;
    }

    public String getDestination() {
        return destination;
    }
}
