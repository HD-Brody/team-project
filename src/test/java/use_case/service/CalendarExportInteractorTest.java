package use_case.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.Assessment;
import entity.ScheduleEvent;
import entity.SourceKind;
import entity.AssessmentType;
import java.util.ArrayList;
import java.util.List;
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
 * Unit tests for {@link CalendarExportInteractor}.
 */
class CalendarExportInteractorTest {

    private StubAssessmentRepository assessmentRepository;
    private StubScheduleEventRepository scheduleEventRepository;
    private RecordingRenderPort renderPort;
    private StubOutputPort outputPort;
    private CalendarExportInteractor service;

    @BeforeEach
    void setUp() {
        assessmentRepository = new StubAssessmentRepository();
        scheduleEventRepository = new StubScheduleEventRepository();
        renderPort = new RecordingRenderPort();
        outputPort = new StubOutputPort();
        service = new CalendarExportInteractor(assessmentRepository, scheduleEventRepository, renderPort, outputPort);
    }

    @Test
    void usesProvidedEventsWhenPresent() {
        ScheduleEvent event = new ScheduleEvent(
                "event-1",
                "user-1",
                "Midterm",
                "2026-02-10T15:00:00Z",
                "2026-02-10T16:30:00Z",
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
                "2026-03-01T15:00:00Z",
                "2026-03-01T16:00:00Z",
                "BA 3155",
                "Bring diagrams",
                SourceKind.ASSESSMENT,
                "assessment-42"
        );
        ScheduleEvent event2 = new ScheduleEvent(
                "event-2",
                "user-22",
                "Demo Day",
                "2026-03-05T19:00:00Z",
                "2026-03-05T20:30:00Z",
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
                0.0,
                "2026-02-14T20:00:00Z",
                null,
                90L,
                15.0,
                "Online",
                "Submit PDF"
        );
        assessmentRepository.addAssessment(assessment);

        ScheduleEvent lectureEvent = new ScheduleEvent(
                "event-3",
                "user-1",
                "Guest Lecture",
                "2026-02-12T18:00:00Z",
                "2026-02-12T19:00:00Z",
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

    @Test
    void generatesPreviewTextsWithTypeFiltering() {
        Assessment assessment = new Assessment(
                "assessment-1",
                "CSC207",
                "Design Doc",
                AssessmentType.ASSIGNMENT,
                0.0,
                "2026-03-01T17:00:00Z",
                null,
                60L,
                10.0,
                "Online",
                "Submit PDF"
        );
        assessmentRepository.addAssessment(assessment);

        ScheduleEvent scheduled = new ScheduleEvent(
                "event-77",
                "user-1",
                "Team Meeting",
                "2026-02-28T15:00:00Z",
                "2026-02-28T16:00:00Z",
                "BA 1130",
                "Finalize slides",
                SourceKind.TASK,
                "task-44"
        );
        scheduleEventRepository.addEvent(scheduled);

        CalendarExportRequest request = new CalendarExportRequest(
                "user-1",
                "UTC",
                List.of("CSC207"),
                null,
                null,
                List.of(),
                "preview"
        );

        List<String> allPreviews = service.generatePreviewTexts(request, PreviewType.ALL);
        assertEquals(2, allPreviews.size());
        assertTrue(allPreviews.get(0).contains("Design Doc"));
        assertTrue(allPreviews.get(1).contains("Team Meeting"));

        List<String> assessmentOnly = service.generatePreviewTexts(request, PreviewType.ASSESSMENT);
        assertEquals(1, assessmentOnly.size());
        assertTrue(assessmentOnly.get(0).contains("[ASSESSMENT]"));

        List<String> scheduleOnly = service.generatePreviewTexts(request, PreviewType.SCHEDULE_EVENT);
        assertEquals(1, scheduleOnly.size());
        assertTrue(scheduleOnly.get(0).contains("[TASK]"));
    }

    private static final class StubAssessmentRepository implements AssessmentRepository {
        private final List<Assessment> assessments = new ArrayList<>();
        private boolean called;

        @Override
        public List<Assessment> findByCourseId(String courseId) {
            called = true;
            return assessments.stream()
                    .filter(a -> a.getCourseId().equals(courseId))
                    .collect(Collectors.toList());
        }

        @Override
        public void save(Assessment assessment) {
            assessments.add(assessment);
        }

        @Override
        public java.util.Optional<Assessment> findById(String assessmentId) {
            return assessments.stream()
                    .filter(a -> a.getAssessmentId().equals(assessmentId))
                    .findFirst();
        }

        @Override
        public void update(Assessment assessment) {
            assessments.removeIf(a -> a.getAssessmentId().equals(assessment.getAssessmentId()));
            assessments.add(assessment);
        }

        @Override
        public void deleteById(String assessmentId) {
            assessments.removeIf(a -> a.getAssessmentId().equals(assessmentId));
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
                    .filter(e -> java.util.Objects.equals(e.getUserId(), userId))
                    .collect(Collectors.toList());
        }

        @Override
        public void save(ScheduleEvent event) {
            this.events.add(event);
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

    private static final class StubOutputPort implements use_case.port.outgoing.CalendarExportOutputPort {
        @Override
        public void presentExport(use_case.dto.CalendarExportResponse response) {
            // No-op for tests
        }

        @Override
        public void presentError(String errorMessage) {
            // No-op for tests
        }
    }
}
