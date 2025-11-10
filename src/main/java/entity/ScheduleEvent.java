package entity;

import java.time.Instant;
import java.util.Objects;

/**
 * Calendar-based representation of tasks or assessments.
 */
public final class ScheduleEvent {
    private final String eventId;
    private final String userId;
    private final String title;
    private final Instant startsAt;
    private final Instant endsAt;
    private final String location;
    private final String notes;
    private final SourceKind source;
    private final String sourceId;

    public ScheduleEvent(String eventId, String userId, String title, Instant startsAt,
                         Instant endsAt, String location, String notes, SourceKind source,
                         String sourceId) {
        this.eventId = Objects.requireNonNull(eventId, "eventId");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.title = Objects.requireNonNull(title, "title");
        this.startsAt = Objects.requireNonNull(startsAt, "startsAt");
        this.endsAt = Objects.requireNonNull(endsAt, "endsAt");
        this.location = location;
        this.notes = notes;
        this.source = Objects.requireNonNull(source, "source");
        this.sourceId = Objects.requireNonNull(sourceId, "sourceId");
    }

    public String getEventId() {
        return eventId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public Instant getStartsAt() {
        return startsAt;
    }

    public Instant getEndsAt() {
        return endsAt;
    }

    public String getLocation() {
        return location;
    }

    public String getNotes() {
        return notes;
    }

    public SourceKind getSource() {
        return source;
    }

    public String getSourceId() {
        return sourceId;
    }
}
