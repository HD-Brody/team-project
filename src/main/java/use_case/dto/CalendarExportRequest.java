package use_case.dto;

import entity.ScheduleEvent;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Parameters required to generate a calendar artifact, including optional filters and
 * pre-supplied events for isolated usage.
 */
public final class CalendarExportRequest {
    private final String userId;
    private final String timezoneId;
    private final List<String> courseIds;
    private final Instant windowStart;
    private final Instant windowEnd;
    private final List<ScheduleEvent> events;
    private final String filenamePrefix;

    public CalendarExportRequest(String userId,
                                 String timezoneId,
                                 List<String> courseIds,
                                 Instant windowStart,
                                 Instant windowEnd,
                                 List<ScheduleEvent> events,
                                 String filenamePrefix) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.timezoneId = Objects.requireNonNull(timezoneId, "timezoneId");
        this.courseIds = courseIds == null ? List.of() : List.copyOf(courseIds);
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.events = events == null ? List.of() : List.copyOf(events);
        this.filenamePrefix = filenamePrefix == null || filenamePrefix.isBlank()
                ? "schedule"
                : filenamePrefix;
    }

    public String getUserId() {
        return userId;
    }

    public String getTimezoneId() {
        return timezoneId;
    }

    public List<String> getCourseIds() {
        return Collections.unmodifiableList(courseIds);
    }

    public Optional<Instant> getWindowStart() {
        return Optional.ofNullable(windowStart);
    }

    public Optional<Instant> getWindowEnd() {
        return Optional.ofNullable(windowEnd);
    }

    public List<ScheduleEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public String getFilenamePrefix() {
        return filenamePrefix;
    }
}
