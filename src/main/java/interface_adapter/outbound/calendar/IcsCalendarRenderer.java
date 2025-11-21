package interface_adapter.outbound.calendar;

import entity.ScheduleEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import net.fortuna.ical4j.validate.ValidationException;
import use_case.dto.CalendarRenderRequest;
import use_case.dto.CalendarRenderResult;
import use_case.port.outgoing.CalendarRenderPort;

/**
 * Renders {@link ScheduleEvent} collections into standards-compliant ICS files using iCal4j.
 */
public class IcsCalendarRenderer implements CalendarRenderPort {
    private static final String CONTENT_TYPE = "text/calendar";
    private final Clock clock;
    private final TimeZoneRegistry timeZoneRegistry;

    public IcsCalendarRenderer() {
        this(Clock.systemUTC(), TimeZoneRegistryFactory.getInstance().createRegistry());
    }

    public IcsCalendarRenderer(Clock clock) {
        this(clock, TimeZoneRegistryFactory.getInstance().createRegistry());
    }

    IcsCalendarRenderer(Clock clock, TimeZoneRegistry timeZoneRegistry) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.timeZoneRegistry = Objects.requireNonNull(timeZoneRegistry, "timeZoneRegistry");
    }

    @Override
    public CalendarRenderResult render(CalendarRenderRequest request) {
        Objects.requireNonNull(request, "request");
        List<ScheduleEvent> events = request.getEvents();
        if (events.isEmpty()) {
            throw new IllegalArgumentException("Cannot render calendar without events");
        }

        Calendar calendar = new Calendar();
        calendar.add(new ProdId(request.getProductId()));
        calendar.add(ImmutableVersion.VERSION_2_0);
        calendar.add(ImmutableCalScale.GREGORIAN);

        TimeZone timeZone = resolveTimeZone(request.getZoneId());
        if (timeZone != null) {
            calendar.add(timeZone.getVTimeZone());
        }

        for (ScheduleEvent event : events) {
            calendar.add(toVEvent(event, timeZone));
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CalendarOutputter outputter = new CalendarOutputter();
        try {
            outputter.output(calendar, outputStream);
        } catch (IOException | ValidationException e) {
            throw new IllegalStateException("Unable to render calendar to ICS", e);
        }

        String filename = sanitizeFilename(request.getFilenameHint()) + ".ics";
        return new CalendarRenderResult(outputStream.toByteArray(), filename, CONTENT_TYPE);
    }

    private VEvent toVEvent(ScheduleEvent event, TimeZone timeZone) {
        Objects.requireNonNull(event, "event");
        ZoneId zoneId = timeZone == null
                ? ZoneId.of("UTC")
                : ZoneId.of(timeZone.getID());
        ZonedDateTime start = event.getStartsAt().atZone(zoneId);
        ZonedDateTime end = event.getEndsAt().atZone(zoneId);
        VEvent vEvent = new VEvent(start, end, event.getTitle());

        vEvent.add(new Uid(event.getEventId()));
        vEvent.add(new DtStamp(clock.instant()));
        if (event.getLocation() != null && !event.getLocation().isBlank()) {
            vEvent.add(new Location(event.getLocation()));
        }
        if (event.getNotes() != null && !event.getNotes().isBlank()) {
            vEvent.add(new Description(event.getNotes()));
        }
        vEvent.add(new Categories(event.getSource().name()));
        return vEvent;
    }

    private TimeZone resolveTimeZone(ZoneId zoneId) {
        TimeZone timeZone = timeZoneRegistry.getTimeZone(zoneId.getId());
        if (timeZone == null) {
            timeZone = timeZoneRegistry.getTimeZone("UTC");
        }
        return timeZone;
    }

    private String sanitizeFilename(String hint) {
        String sanitized = hint == null || hint.isBlank()
                ? "schedule"
                : hint.replaceAll("[^a-zA-Z0-9-_]", "_");
        return sanitized.toLowerCase(Locale.ROOT);
    }
}
