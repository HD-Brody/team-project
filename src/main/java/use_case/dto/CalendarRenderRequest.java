package use_case.dto;

import entity.ScheduleEvent;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Command object describing how to render a collection of events into a calendar artifact.
 */
public final class CalendarRenderRequest {
    private final String productId;
    private final ZoneId zoneId;
    private final String filenameHint;
    private final List<ScheduleEvent> events;

    public CalendarRenderRequest(String productId, ZoneId zoneId, String filenameHint,
                                 List<ScheduleEvent> events) {
        this.productId = Objects.requireNonNull(productId, "productId");
        this.zoneId = Objects.requireNonNull(zoneId, "zoneId");
        this.filenameHint = filenameHint == null || filenameHint.isBlank()
                ? "schedule"
                : filenameHint;
        this.events = List.copyOf(Objects.requireNonNull(events, "events"));
    }

    public String getProductId() {
        return productId;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public String getFilenameHint() {
        return filenameHint;
    }

    public List<ScheduleEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }
}
