package interface_adapter.outbound.calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.ScheduleEvent;
import entity.SourceKind;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;
import org.junit.jupiter.api.Test;
import use_case.dto.CalendarRenderRequest;

/**
 * Integration-style tests for {@link IcsCalendarRenderer}.
 */
class IcsCalendarRendererTest {

    @Test
    void rendersValidIcsFile() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-02-01T12:00:00Z"), ZoneId.of("UTC"));
        IcsCalendarRenderer renderer = new IcsCalendarRenderer(clock);

        ScheduleEvent event = new ScheduleEvent(
                "event-42",
                "user-7",
                "Capstone Presentation",
                Instant.parse("2026-03-10T15:00:00Z"),
                Instant.parse("2026-03-10T16:00:00Z"),
                "BA 2135",
                "Slides due 24h before",
                SourceKind.ASSESSMENT,
                "assessment-99"
        );

        CalendarRenderRequest renderRequest = new CalendarRenderRequest(
                "-//MARBLE//Calendar Export//EN",
                ZoneId.of("America/Toronto"),
                "Capstone Schedule",
                List.of(event)
        );

        var result = renderer.render(renderRequest);

        assertTrue(result.getFilename().endsWith(".ics"));
        assertEquals("text/calendar", result.getContentType());

        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(new ByteArrayInputStream(result.getPayload()));
        VEvent renderedEvent = (VEvent) calendar.getComponents(Component.VEVENT).get(0);

        assertEquals("Capstone Presentation",
                renderedEvent.getSummary().map(Summary::getValue).orElseThrow());
        assertEquals("BA 2135",
                renderedEvent.getLocation()
                        .map(Location::getValue)
                        .orElseThrow());
        assertEquals("Slides due 24h before",
                renderedEvent.getDescription()
                        .map(Description::getValue)
                        .orElseThrow());
    }

    @Test
    void rejectsEmptyEventCollections() {
        IcsCalendarRenderer renderer = new IcsCalendarRenderer();
        CalendarRenderRequest renderRequest = new CalendarRenderRequest(
                "-//MARBLE//Calendar Export//EN",
                ZoneId.of("UTC"),
                "Empty",
                List.of()
        );

        assertThrows(IllegalArgumentException.class, () -> renderer.render(renderRequest));
    }

    @Test
    void writesFixtureFileForManualInspection() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-02-01T12:00:00Z"), ZoneId.of("UTC"));
        IcsCalendarRenderer renderer = new IcsCalendarRenderer(clock);
        ScheduleEvent event = new ScheduleEvent(
                "fixture-1",
                "user-99",
                "Fixture Event",
                Instant.parse("2026-04-01T12:00:00Z"),
                Instant.parse("2026-04-01T13:00:00Z"),
                "BA 2000",
                "This file is for manual inspection.",
                SourceKind.ASSESSMENT,
                "assessment-fixture"
        );
        CalendarRenderRequest request = new CalendarRenderRequest(
                "-//MARBLE//Calendar Export//EN",
                ZoneId.of("America/Toronto"),
                "fixture-calendar",
                List.of(event)
        );

        var result = renderer.render(request);

        Path fixtureDir = Path.of("src/test/resources/fixtures");
        Files.createDirectories(fixtureDir);
        Path fixtureFile = fixtureDir.resolve("ics-calendar-renderer-output.ics");
        Files.write(fixtureFile, result.getPayload());

        assertTrue(Files.exists(fixtureFile), "Fixture file should exist for manual inspection");
        assertTrue(Files.size(fixtureFile) > 0, "Fixture file should not be empty");
    }
}
