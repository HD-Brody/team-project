package use_case.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.Assessment;
import entity.ScheduleEvent;
import entity.SourceKind;
import entity.AssessmentType;
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
import use_case.port.outgoing.CalendarRenderPort;
import use_case.repository.AssessmentRepository;
import use_case.repository.ScheduleEventRepository;

/**
 * Unit tests for {@link CalendarExportService}.
 */
class CalendarExportServiceTest {

    private StubAssessmentRepository assessmentRepository;
    private StubScheduleEventRepository scheduleEventRepository;
    private RecordingRenderPort renderPort;
    private CalendarExportService service;

    @BeforeEach
    void setUp() {
        assessmentRepository = new StubAssessmentRepository();
        scheduleEventRepository = new StubScheduleEventRepository();
        renderPort = new RecordingRenderPort();
        service = new CalendarExportService(assessmentRepository, scheduleEventRepository, renderPort);
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
        assertFalse(assessmentRepository.wasCalled());
        assertFalse(scheduleEventRepository.wasCalled());
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
        assertFalse(assessmentRepository.wasCalled());
        assertFalse(scheduleEventRepository.wasCalled());
    }

    @Test
    void loadsAssessmentsAndEventsWhenRequestHasNoEvents() {
        Assessment assessment = new Assessment(
                "assessment-1",
                "CSC207",
                "Project Milestone",
                AssessmentType.ASSIGNMENT,
                Instant.parse("2026-02-14T20:00:00Z"),
                null,
                90L,
                15.0,
                null,
                "Online",
                "Submit PDF"
        );
        assessmentRepository.addAssessment(assessment);

        ScheduleEvent lectureEvent = new ScheduleEvent(
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
        scheduleEventRepository.addEvent(lectureEvent);

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
        assertTrue(assessmentRepository.wasCalled());
        assertTrue(scheduleEventRepository.wasCalled());
        assertEquals(2, renderPort.lastRequest.getEvents().size());
        ScheduleEvent assessmentEvent = renderPort.lastRequest.getEvents().get(0);
        assertTrue(assessmentEvent.getNotes().contains("Weight: 15.00%"));
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

    private static final class StubAssessmentRepository implements AssessmentRepository {
        private final List<Assessment> assessments = new ArrayList<>();
        private boolean called;

        @Override
        public Optional<Assessment> findById(String assessmentId) {
            called = true;
            return assessments.stream()
                    .filter(a -> a.getAssessmentId().equals(assessmentId))
                    .findFirst();
        }

        @Override
        public List<Assessment> findByCourseId(String courseId) {
            called = true;
            return assessments.stream()
                    .filter(a -> a.getCourseId().equals(courseId))
                    .collect(Collectors.toList());
        }

        @Override
        public void saveAll(List<Assessment> assessments) {
            this.assessments.addAll(assessments);
        }

        void addAssessment(Assessment assessment) {
            assessments.add(assessment);
        }

        boolean wasCalled() {
            return called;
        }
    }

    private static final class StubScheduleEventRepository implements ScheduleEventRepository {
        private final List<ScheduleEvent> events = new ArrayList<>();
        private boolean called;

        @Override
        public List<ScheduleEvent> findByUserId(String userId) {
            called = true;
            return events.stream()
                    .filter(e -> e.getUserId().equals(userId))
                    .collect(Collectors.toList());
        }

        @Override
        public void saveAll(List<ScheduleEvent> events) {
            this.events.addAll(events);
        }

        void addEvent(ScheduleEvent event) {
            events.add(event);
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
