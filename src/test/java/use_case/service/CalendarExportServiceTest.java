package use_case.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.Assessment;
import entity.ScheduleEvent;
import entity.SourceKind;
import entity.AssessmentType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.time.Instant;
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
 * Unit tests for {@link CalendarExportService}.
 */
class CalendarExportServiceTest {

    private StubAssessmentRepository assessmentRepository;
    private StubScheduleEventRepository scheduleEventRepository;
    private RecordingRenderPort renderPort;
    private CapturingOutputPort outputPort;
    private CalendarExportService service;

    @BeforeEach
    void setUp() {
        assessmentRepository = new StubAssessmentRepository();
        scheduleEventRepository = new StubScheduleEventRepository();
        renderPort = new RecordingRenderPort();
        outputPort = new CapturingOutputPort();
        service = new CalendarExportService(assessmentRepository, scheduleEventRepository, renderPort, outputPort);
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

        CalendarExportResponse response = service.exportCalendar(request);
        assertNull(response);
        assertTrue(outputPort.lastError.contains("Unsupported timezone"));
    }

    @Test
    void noEventsTriggersFriendlyError() {
        CalendarExportRequest request = new CalendarExportRequest(
                "user-404",
                "UTC",
                List.of(),
                null,
                null,
                List.of(),
                "empty"
        );

        CalendarExportResponse response = service.exportCalendar(request);

        assertNull(response);
        assertTrue(outputPort.lastError.contains("No exportable events"));
    }

    @Test
    void rendererFailureIsReported() {
        renderPort.fail = true;
        ScheduleEvent event = new ScheduleEvent(
                "event-render",
                "user-r",
                "Throwing event",
                "2026-04-01T10:00:00Z",
                "2026-04-01T11:00:00Z",
                null,
                null,
                SourceKind.TASK,
                "task-r"
        );

        CalendarExportRequest request = new CalendarExportRequest(
                "user-r",
                "UTC",
                List.of(),
                null,
                null,
                List.of(event),
                "failme"
        );

        CalendarExportResponse response = service.exportCalendar(request);

        assertNull(response);
        assertTrue(outputPort.lastError.contains("Failed to export calendar"));
    }

    @Test
    void assessmentsFallbackToDueDateAndWindowFilter() {
        Assessment withDueOnly = new Assessment(
                "a-due",
                "CSC207",
                "Due Only",
                AssessmentType.ASSIGNMENT,
                0.0,
                null,
                "2026-05-10T12:00:00Z",
                null,
                null,
                null,
                "notes"
        );
        Assessment weightOnlyNotes = new Assessment(
                "a-weight",
                "CSC207",
                "Weight Only",
                AssessmentType.QUIZ,
                0.0,
                "2026-05-15T09:00:00Z",
                null,
                null,
                5.0,
                null,
                null
        );
        // This one should be filtered out by window end
        Assessment outsideWindow = new Assessment(
                "a-out",
                "CSC207",
                "Outside Window",
                AssessmentType.ASSIGNMENT,
                0.0,
                null,
                "2028-01-01T12:00:00Z",
                null,
                null,
                null,
                null
        );
        assessmentRepository.addAssessment(withDueOnly);
        assessmentRepository.addAssessment(weightOnlyNotes);
        assessmentRepository.addAssessment(outsideWindow);

        CalendarExportRequest request = new CalendarExportRequest(
                "user-1",
                "UTC",
                List.of("CSC207"),
                java.time.Instant.parse("2026-05-01T00:00:00Z"),
                java.time.Instant.parse("2026-12-31T00:00:00Z"),
                List.of(),
                "windowed"
        );

        CalendarExportResponse response = service.exportCalendar(request);

        assertEquals(2, response.getEventCount(), "Two assessments are within the window");
        ScheduleEvent exported = renderPort.lastRequest.getEvents().get(0);
        assertTrue(exported.getEventId().startsWith("assessment-a-due"));
        assertTrue(exported.getStartsAt().compareTo(exported.getEndsAt()) < 0, "Start should be before end");
        ScheduleEvent weighted = renderPort.lastRequest.getEvents().get(1);
        assertTrue(weighted.getNotes().contains("Weight: 5.00%"));
    }

    @Test
    void composeEventsDedupeAndFilterInvalidStart() {
        // Duplicate provided events should dedupe
        ScheduleEvent dupe = new ScheduleEvent(
                "dupe",
                "user-dup",
                "Duplicate",
                "2026-06-01T10:00:00Z",
                "2026-06-01T11:00:00Z",
                "",
                "",
                SourceKind.TASK,
                "task-dup"
        );
        // Invalid start date should be filtered out of window list
        ScheduleEvent invalidStart = new ScheduleEvent(
                "bad-date",
                "user-dup",
                "Bad Date",
                "not-a-date",
                "2026-06-02T10:00:00Z",
                null,
                null,
                SourceKind.TASK,
                "task-bad"
        );
        scheduleEventRepository.addEvent(invalidStart);

        CalendarExportRequest request = new CalendarExportRequest(
                "user-dup",
                "UTC",
                List.of(),
                null,
                null,
                List.of(dupe, dupe),
                "dedupe"
        );

        CalendarExportResponse response = service.exportCalendar(request);

        assertEquals(1, response.getEventCount(), "Duplicate IDs should be merged");
        assertEquals(1, renderPort.lastRequest.getEvents().size());
        assertFalse(scheduleEventRepository.wasCalled(), "Provided events path should skip repo");
    }

    @Test
    void filtersOutScheduleEventsWithInvalidStarts() {
        ScheduleEvent invalidStart = new ScheduleEvent(
                "invalid",
                "user-bad",
                "Bad Start",
                "not-a-date",
                "2026-07-01T01:00:00Z",
                null,
                null,
                SourceKind.TASK,
                "task-bad"
        );
        ScheduleEvent valid = new ScheduleEvent(
                "valid",
                "user-bad",
                "Good Start",
                "2026-07-02T01:00:00Z",
                "2026-07-02T02:00:00Z",
                null,
                null,
                SourceKind.TASK,
                "task-good"
        );
        scheduleEventRepository.addEvent(invalidStart);
        scheduleEventRepository.addEvent(valid);

        CalendarExportRequest request = new CalendarExportRequest(
                "user-bad",
                "UTC",
                List.of(),
                null,
                null,
                List.of(),
                "filter"
        );

        CalendarExportResponse response = service.exportCalendar(request);

        assertEquals(1, response.getEventCount(), "Invalid start should be dropped");
        assertEquals("valid", renderPort.lastRequest.getEvents().get(0).getEventId());
    }

    @Test
    void assessmentsWithoutDatesAreSkippedAndEndsAtPresentBranch() {
        Assessment noDates = new Assessment(
                "a-nodate",
                "CSC207",
                "No Dates",
                AssessmentType.PROJECT,
                0.0,
                null,
                null,
                null,
                10.0,
                null,
                null
        );
        Assessment withStartsAndEnds = new Assessment(
                "a-both",
                "CSC207",
                "Both Times",
                AssessmentType.QUIZ,
                0.0,
                "2026-08-01T10:00:00Z",
                "2026-08-01T11:00:00Z",
                null,
                5.0,
                "Room 100",
                "quiz"
        );
        assessmentRepository.addAssessment(noDates);
        assessmentRepository.addAssessment(withStartsAndEnds);

        CalendarExportRequest request = new CalendarExportRequest(
                "user-both",
                "UTC",
                List.of("CSC207"),
                null,
                null,
                List.of(),
                "both"
        );

        CalendarExportResponse response = service.exportCalendar(request);

        assertEquals(1, response.getEventCount(), "No-dates assessment should be skipped");
        ScheduleEvent exported = renderPort.lastRequest.getEvents().get(0);
        assertEquals("assessment-a-both", exported.getEventId());
        assertEquals("Room 100", exported.getLocation());
    }

    @Test
    void previewLineIncludesLocationAndWindowFilterFalseBranch() {
        ScheduleEvent outsideWindow = new ScheduleEvent(
                "outside",
                "user-window",
                "Outside",
                "2026-09-01T10:00:00Z",
                "2026-09-01T11:00:00Z",
                "Somewhere",
                "",
                SourceKind.TASK,
                "task-out"
        );
        scheduleEventRepository.addEvent(outsideWindow);

        CalendarExportRequest request = new CalendarExportRequest(
                "user-window",
                "UTC",
                List.of(),
                Instant.parse("2026-10-01T00:00:00Z"),
                Instant.parse("2026-10-31T00:00:00Z"),
                List.of(),
                "window"
        );

        List<String> previews = service.generatePreviewTexts(request, PreviewType.ALL);
        assertTrue(previews.isEmpty(), "Outside window should be filtered out");

        // Direct preview line for location branch
        String preview = service.generatePreviewTexts(
                new CalendarExportRequest(
                        "user-window",
                        "UTC",
                        List.of(),
                        null,
                        null,
                        List.of(outsideWindow),
                        "window"), PreviewType.ALL).get(0);
        assertTrue(preview.contains("@ Somewhere"));
    }

    @Test
    void notesAndLocationBranchesAreCovered() {
        Assessment weightOnlyNotes = new Assessment(
                "a-weightonly",
                "CSC207",
                "Weight-Only",
                AssessmentType.QUIZ,
                0.0,
                "2026-11-01T10:00:00Z",
                "2026-11-01T11:00:00Z",
                null,
                20.0,
                null,
                null
        );
        assessmentRepository.addAssessment(weightOnlyNotes);

        ScheduleEvent withLocation = new ScheduleEvent(
                "event-loc",
                "user-loc",
                "Has Location",
                "2026-11-02T09:00:00Z",
                "2026-11-02T10:00:00Z",
                "BA 100",
                "",
                SourceKind.TASK,
                "task-loc"
        );

        CalendarExportRequest request = new CalendarExportRequest(
                "user-loc",
                "UTC",
                List.of("CSC207"),
                null,
                null,
                List.of(withLocation),
                "loc"
        );

        service.exportCalendar(request);

        // Force load of assessments by omitting provided events in second call
        // Trigger repository path by omitting provided events and seed the repo event
        scheduleEventRepository.addEvent(withLocation);
        CalendarExportResponse resp2 = service.exportCalendar(new CalendarExportRequest(
                "user-loc",
                "UTC",
                List.of("CSC207"),
                null,
                null,
                null,
                "loc2"
        ));
        assertEquals(2, resp2.getEventCount(), "Weight-only assessment + stored event");

        ScheduleEvent assessmentEvent = renderPort.lastRequest.getEvents().stream()
                .filter(e -> e.getEventId().startsWith("assessment-a-weightonly"))
                .findFirst()
                .orElseThrow();
        assertTrue(assessmentEvent.getNotes().contains("Weight: 20.00%"), "Weight should be appended when notes blank");

        ScheduleEvent locEvent = renderPort.lastRequest.getEvents().stream()
                .filter(e -> e.getEventId().equals("event-loc"))
                .findFirst()
                .orElseThrow();
        String preview = service.generatePreviewTexts(
                new CalendarExportRequest(
                        "user-loc",
                        "UTC",
                        List.of(),
                        null,
                        null,
                        List.of(locEvent),
                        "preview"), PreviewType.ALL).get(0);
        assertTrue(preview.contains("@ BA 100"), "Preview should include location when present");
    }

    @Test
    void helperMethodsCoverage() throws Exception {
        // resolveEnd branches
        Method resolveEnd = CalendarExportService.class.getDeclaredMethod("resolveEnd", Assessment.class, Instant.class);
        resolveEnd.setAccessible(true);
        Assessment withEnds = new Assessment("a1", "c1", "title", AssessmentType.EXAM, 0.0,
                "2026-01-01T00:00:00Z", "2026-01-02T00:00:00Z", null, null, null, null);
        String end1 = (String) resolveEnd.invoke(service, withEnds, Instant.parse("2026-01-01T00:00:00Z"));
        assertEquals("2026-01-02T00:00:00Z", end1);

        Assessment withDuration = new Assessment("a2", "c1", "title", AssessmentType.EXAM, 0.0,
                "2026-01-01T00:00:00Z", null, 90L, null, null, null);
        String end2 = (String) resolveEnd.invoke(service, withDuration, Instant.parse("2026-01-01T00:00:00Z"));
        assertTrue(end2.contains("T01:30:00Z") || end2.contains("T01:30:00"), "Should add duration to start");

        Assessment withDefault = new Assessment("a3", "c1", "title", AssessmentType.EXAM, 0.0,
                "2026-01-01T00:00:00Z", null, null, null, null, null);
        String end3 = (String) resolveEnd.invoke(service, withDefault, Instant.parse("2026-01-01T00:00:00Z"));
        assertTrue(end3.contains("T01:00:00Z") || end3.contains("T01:00:00"), "Should default to +1h");

        Assessment withBlankEnds = new Assessment("a4", "c1", "title", AssessmentType.EXAM, 0.0,
                "2026-01-01T00:00:00Z", "", null, null, null, null);
        String end4 = (String) resolveEnd.invoke(service, withBlankEnds, Instant.parse("2026-01-01T00:00:00Z"));
        assertTrue(end4.contains("T01:00:00Z") || end4.contains("T01:00:00"), "Blank endsAt should default");

        // parseInstant branches
        Method parseInstant = CalendarExportService.class.getDeclaredMethod("parseInstant", String.class);
        parseInstant.setAccessible(true);
        assertTrue(((java.util.Optional<?>) parseInstant.invoke(service, (Object) null)).isEmpty());
        assertTrue(((java.util.Optional<?>) parseInstant.invoke(service, "")).isEmpty());
        assertTrue(((java.util.Optional<?>) parseInstant.invoke(service, "not-a-date")).isEmpty());
        assertTrue(((java.util.Optional<?>) parseInstant.invoke(service, "2026-02-01T10:00:00Z")).isPresent());
        assertTrue(((java.util.Optional<?>) parseInstant.invoke(service, "2026-02-01T10:00:00+01:00")).isPresent());

        // withinWindow branches
        Method withinWindow = CalendarExportService.class.getDeclaredMethod("withinWindow", Instant.class, java.util.Optional.class, java.util.Optional.class);
        withinWindow.setAccessible(true);
        assertFalse((Boolean) withinWindow.invoke(service, null, java.util.Optional.empty(), java.util.Optional.empty()));
        assertFalse((Boolean) withinWindow.invoke(service, Instant.parse("2026-01-01T00:00:00Z"),
                java.util.Optional.of(Instant.parse("2026-01-02T00:00:00Z")), java.util.Optional.empty()));
        assertFalse((Boolean) withinWindow.invoke(service, Instant.parse("2026-01-03T00:00:00Z"),
                java.util.Optional.empty(), java.util.Optional.of(Instant.parse("2026-01-02T00:00:00Z"))));
        assertTrue((Boolean) withinWindow.invoke(service, Instant.parse("2026-01-02T00:00:00Z"),
                java.util.Optional.of(Instant.parse("2026-01-01T00:00:00Z")), java.util.Optional.of(Instant.parse("2026-01-03T00:00:00Z"))));

        // composeEvents branches
        Method composeEvents = CalendarExportService.class.getDeclaredMethod("composeEvents", List[].class);
        composeEvents.setAccessible(true);
        ScheduleEvent e1 = new ScheduleEvent("dup", "u", "One", "2026-01-01T00:00:00Z", "2026-01-01T01:00:00Z", null, null, SourceKind.TASK, "t1");
        List<ScheduleEvent> merged = (List<ScheduleEvent>) composeEvents.invoke(service, (Object) new List[]{null, new ArrayList<>(Arrays.asList(e1, null)), List.of(e1)});
        assertEquals(1, merged.size());
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
        private boolean fail;

        @Override
        public CalendarRenderResult render(CalendarRenderRequest request) {
            this.lastRequest = request;
            if (fail) {
                throw new IllegalStateException("Renderer failure");
            }
            return new CalendarRenderResult("BEGIN:VCALENDAR".getBytes(), "test.ics", "text/calendar");
        }
    }

    private static final class CapturingOutputPort implements use_case.port.outgoing.CalendarExportOutputPort {
        private String lastError = "";
        private use_case.dto.CalendarExportResponse lastResponse;

        @Override
        public void presentExport(use_case.dto.CalendarExportResponse response) {
            this.lastResponse = response;
        }

        @Override
        public void presentError(String errorMessage) {
            this.lastError = errorMessage;
        }
    }
}
