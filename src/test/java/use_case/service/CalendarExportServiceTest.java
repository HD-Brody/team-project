package use_case.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.ScheduleEvent;
import entity.SourceKind;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.dto.CalendarExportRequest;
import use_case.dto.CalendarExportResponse;
import use_case.dto.CalendarRenderRequest;
import use_case.dto.CalendarRenderResult;
import use_case.dto.ScheduleEventSnapshot;
import use_case.dto.ScheduledTaskSnapshot;
import use_case.port.outgoing.CalendarRenderPort;
import use_case.port.outgoing.ScheduleEventQueryPort;
import use_case.port.outgoing.ScheduledTaskQueryPort;

/**
 * Unit tests for {@link CalendarExportService}.
 */
class CalendarExportServiceTest {

    private StubScheduledTaskQueryPort scheduledTaskQueryPort;
    private StubScheduleEventQueryPort scheduleEventQueryPort;
    private RecordingRenderPort renderPort;
    private CalendarExportService service;

    @BeforeEach
    void setUp() {
        scheduledTaskQueryPort = new StubScheduledTaskQueryPort();
        scheduleEventQueryPort = new StubScheduleEventQueryPort();
        renderPort = new RecordingRenderPort();
        service = new CalendarExportService(scheduledTaskQueryPort, scheduleEventQueryPort, renderPort);
    }

    @Test
    void usesProvidedEventsWhenPresent() {
        ScheduleEvent event = new ScheduleEvent(
                "event-1",
                "user-1",
                "Midterm",
                Instant.parse("2026-02-10T15:00:00Z"),
                Instant.parse("2026-02-10T16:30:00Z"),
                "BA 1130",
                "Bring calculator",
                SourceKind.ASSESSMENT,
                "assessment-9"
        );

        CalendarExportRequest request = new CalendarExportRequest(
                "user-1",
                "UTC",
                List.of(),
                null,
                null,
                List.of(event),
                "My Calendar"
        );

        CalendarExportResponse response = service.exportCalendar(request);

        assertEquals(1, response.getEventCount());
        assertFalse(scheduledTaskQueryPort.wasCalled());
        assertFalse(scheduleEventQueryPort.wasCalled());
        assertEquals(1, renderPort.lastRequest.getEvents().size());
        assertEquals("Midterm", renderPort.lastRequest.getEvents().get(0).getTitle());
    }

    @Test
    void rendersMultipleProvidedEventsWithoutLoadingFromPorts() {
        ScheduleEvent event1 = new ScheduleEvent(
                "event-1",
                "user-22",
                "Design Review",
                Instant.parse("2026-03-01T15:00:00Z"),
                Instant.parse("2026-03-01T16:00:00Z"),
                "BA 3155",
                "Bring diagrams",
                SourceKind.ASSESSMENT,
                "assessment-42"
        );
        ScheduleEvent event2 = new ScheduleEvent(
                "event-2",
                "user-22",
                "Demo Day",
                Instant.parse("2026-03-05T19:00:00Z"),
                Instant.parse("2026-03-05T20:30:00Z"),
                "BA 1130",
                "Guests invited",
                SourceKind.ASSESSMENT,
                "assessment-43"
        );

        CalendarExportRequest request = new CalendarExportRequest(
                "user-22",
                "UTC",
                List.of(),
                null,
                null,
                List.of(event1, event2),
                "Deliverables"
        );

        CalendarExportResponse response = service.exportCalendar(request);

        assertEquals(2, response.getEventCount());
        assertEquals(2, renderPort.lastRequest.getEvents().size());
        List<String> renderedIds = renderPort.lastRequest.getEvents().stream()
                .map(ScheduleEvent::getEventId)
                .collect(Collectors.toList());
        assertEquals(List.of("event-1", "event-2"), renderedIds);
        assertFalse(scheduledTaskQueryPort.wasCalled());
        assertFalse(scheduleEventQueryPort.wasCalled());
    }

    @Test
    void loadsTasksAndEventsWhenRequestHasNoEvents() {
        ScheduledTaskSnapshot taskSnapshot = new ScheduledTaskSnapshot(
                "task-1",
                "user-1",
                "CSC207",
                "Project Milestone",
                Instant.parse("2026-02-14T20:00:00Z"),
                120,
                15.0,
                "Online",
                "Submit PDF"
        );
        scheduledTaskQueryPort.addSnapshot(taskSnapshot);

        ScheduleEventSnapshot eventSnapshot = new ScheduleEventSnapshot(
                "event-3",
                "user-1",
                "Guest Lecture",
                Instant.parse("2026-02-12T18:00:00Z"),
                Instant.parse("2026-02-12T19:00:00Z"),
                "BA 1160",
                "Attendance optional",
                SourceKind.ASSESSMENT,
                "assess-77"
        );
        scheduleEventQueryPort.addSnapshot(eventSnapshot);

        CalendarExportRequest request = new CalendarExportRequest(
                "user-1",
                "UTC",
                List.of("CSC207"),
                null,
                null,
                List.of(),
                "schedule"
        );

        CalendarExportResponse response = service.exportCalendar(request);

        assertEquals(2, response.getEventCount());
        assertTrue(scheduledTaskQueryPort.wasCalled());
        assertTrue(scheduleEventQueryPort.wasCalled());
        assertEquals(2, renderPort.lastRequest.getEvents().size());
        ScheduleEvent taskEvent = renderPort.lastRequest.getEvents().get(0);
        assertTrue(taskEvent.getNotes().contains("Weight: 15.00%"));
    }

    @Test
    void rejectsUnknownTimezone() {
        CalendarExportRequest request = new CalendarExportRequest(
                "user-1",
                "Mars/Phobos",
                List.of(),
                null,
                null,
                List.of(),
                "schedule"
        );

        assertThrows(IllegalArgumentException.class, () -> service.exportCalendar(request));
    }

    private static final class StubScheduledTaskQueryPort implements ScheduledTaskQueryPort {
        private final List<ScheduledTaskSnapshot> snapshots = new ArrayList<>();
        private boolean called;

        @Override
        public List<ScheduledTaskSnapshot> findTasksForExport(String userId, List<String> courseIds,
                                                              Optional<Instant> windowStart,
                                                              Optional<Instant> windowEnd) {
            called = true;
            return snapshots;
        }

        void addSnapshot(ScheduledTaskSnapshot snapshot) {
            snapshots.add(snapshot);
        }

        boolean wasCalled() {
            return called;
        }
    }

    private static final class StubScheduleEventQueryPort implements ScheduleEventQueryPort {
        private final List<ScheduleEventSnapshot> snapshots = new ArrayList<>();
        private boolean called;

        @Override
        public List<ScheduleEventSnapshot> findScheduleEvents(String userId, List<String> courseIds,
                                                              Optional<Instant> windowStart,
                                                              Optional<Instant> windowEnd) {
            called = true;
            return snapshots;
        }

        void addSnapshot(ScheduleEventSnapshot snapshot) {
            snapshots.add(snapshot);
        }

        boolean wasCalled() {
            return called;
        }
    }

    private static final class RecordingRenderPort implements CalendarRenderPort {
        private CalendarRenderRequest lastRequest;

        @Override
        public CalendarRenderResult render(CalendarRenderRequest request) {
            this.lastRequest = request;
            return new CalendarRenderResult("BEGIN:VCALENDAR".getBytes(), "test.ics", "text/calendar");
        }
    }
}
