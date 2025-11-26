package use_case.service;

import entity.Assessment;
import entity.ScheduleEvent;
import entity.SourceKind;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import use_case.dto.CalendarExportRequest;
import use_case.dto.CalendarExportResponse;
import use_case.dto.CalendarRenderRequest;
import use_case.dto.CalendarRenderResult;
import use_case.port.incoming.CalendarExportUseCase;
import use_case.port.outgoing.CalendarRenderPort;
import use_case.repository.AssessmentRepository;
import use_case.repository.ScheduleEventRepository;

/**
 * Aggregates assessments and events before exporting them through a calendar renderer.
 */
public class CalendarExportService implements CalendarExportUseCase {
    private static final String DEFAULT_PRODUCT_ID = "-//MARBLE//Calendar Export//EN";
    private static final Duration DEFAULT_DURATION = Duration.ofHours(1);

    private final AssessmentRepository assessmentRepository;
    private final ScheduleEventRepository scheduleEventRepository;
    private final CalendarRenderPort calendarRenderPort;

    public CalendarExportService(AssessmentRepository assessmentRepository,
                                 ScheduleEventRepository scheduleEventRepository,
                                 CalendarRenderPort calendarRenderPort) {
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository,
                "assessmentRepository");
        this.scheduleEventRepository = Objects.requireNonNull(scheduleEventRepository,
                "scheduleEventRepository");
        this.calendarRenderPort = Objects.requireNonNull(calendarRenderPort,
                "calendarRenderPort");
    }

    @Override
    public CalendarExportResponse exportCalendar(CalendarExportRequest request) {
        Objects.requireNonNull(request, "request");

        ZoneId zoneId = parseZone(request.getTimezoneId());
        List<ScheduleEvent> events = resolveEvents(request);

        if (events.isEmpty()) {
            throw new IllegalArgumentException(
                    "No exportable events were found for user " + request.getUserId());
        }

        CalendarRenderRequest renderRequest = new CalendarRenderRequest(
                DEFAULT_PRODUCT_ID,
                zoneId,
                request.getFilenamePrefix(),
                events
        );

        CalendarRenderResult renderResult = calendarRenderPort.render(renderRequest);

        return new CalendarExportResponse(
                renderResult.getPayload(),
                renderResult.getFilename(),
                renderResult.getContentType(),
                events.size(),
                Instant.now()
        );
    }

    /**
     * Builds human-readable preview strings for the events that would be exported.
     * Intended for UI preview panes before generating the ICS file.
     */
    public List<String> generatePreviewTexts(CalendarExportRequest request,
                                             PreviewType previewType) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(previewType, "previewType");

        ZoneId zoneId = parseZone(request.getTimezoneId());
        List<ScheduleEvent> events = resolveEvents(request);
        List<ScheduleEvent> filtered = filterByType(events, previewType);

        return filtered.stream()
                .map(event -> formatPreviewLine(event, zoneId))
                .collect(Collectors.toList());
    }

    private ZoneId parseZone(String timezoneId) {
        try {
            return ZoneId.of(timezoneId);
        } catch (DateTimeException ex) {
            throw new IllegalArgumentException("Unsupported timezone: " + timezoneId, ex);
        }
    }

    private List<ScheduleEvent> resolveEvents(CalendarExportRequest request) {
        List<ScheduleEvent> events = composeEvents(request.getEvents());

        if (events.isEmpty()) {
            events = composeEvents(
                    assessmentsToEvents(loadAssessments(request), request.getUserId(),
                            request.getWindowStart(), request.getWindowEnd()),
                    filterEventsByWindow(loadScheduleEvents(request), request.getWindowStart(),
                            request.getWindowEnd())
            );
        }
        return events;
    }

    private List<Assessment> loadAssessments(CalendarExportRequest request) {
        List<Assessment> assessments = new ArrayList<>();
        for (String courseId : request.getCourseIds()) {
            assessments.addAll(assessmentRepository.findByCourseId(courseId));
        }
        return assessments;
    }

    private List<ScheduleEvent> loadScheduleEvents(CalendarExportRequest request) {
        return scheduleEventRepository.findByUserId(request.getUserId());
    }

    private List<ScheduleEvent> assessmentsToEvents(List<Assessment> assessments,
                                                    String userId,
                                                    Optional<Instant> windowStart,
                                                    Optional<Instant> windowEnd) {
        List<ScheduleEvent> events = new ArrayList<>();
        for (Assessment assessment : assessments) {
            String startsAt = assessment.getStartsAt();
            if (startsAt == null) {
                continue;
            }
            if (!withinWindow(startsAt, windowStart, windowEnd)) {
                continue;
            }

            Instant endsAt = resolveEnd(assessment, startsAt);
            String notes = enrichNotesWithWeight(assessment.getNotes(), assessment.getWeight());

            events.add(new ScheduleEvent(
                    "assessment-" + assessment.getAssessmentId(),
                    userId,
                    assessment.getTitle(),
                    startsAt,
                    endsAt,
                    assessment.getLocation(),
                    notes,
                    SourceKind.ASSESSMENT,
                    assessment.getAssessmentId()
            ));
        }
        return events;
    }

    private Instant resolveEnd(Assessment assessment, Instant startsAt) {
        if (assessment.getEndsAt() != null) {
            return assessment.getEndsAt();
        }
        if (assessment.getDurationMinutes() != null) {
            return startsAt.plus(Duration.ofMinutes(assessment.getDurationMinutes()));
        }
        return startsAt.plus(DEFAULT_DURATION);
    }

    private String enrichNotesWithWeight(String notes, Double weightPercent) {
        if (weightPercent == null) {
            return notes;
        }
        String weightLine = String.format("Weight: %.2f%%", weightPercent);
        if (notes == null || notes.isBlank()) {
            return weightLine;
        }
        return notes + System.lineSeparator() + weightLine;
    }

    private List<ScheduleEvent> filterEventsByWindow(List<ScheduleEvent> events,
                                                     Optional<Instant> windowStart,
                                                     Optional<Instant> windowEnd) {
        List<ScheduleEvent> filtered = new ArrayList<>();
        for (ScheduleEvent event : events) {
            if (withinWindow(event.getStartsAt(), windowStart, windowEnd)) {
                filtered.add(event);
            }
        }
        return filtered;
    }

    private List<ScheduleEvent> filterByType(List<ScheduleEvent> events, PreviewType previewType) {
        if (previewType == PreviewType.ALL) {
            return events;
        }
        List<ScheduleEvent> filtered = new ArrayList<>();
        for (ScheduleEvent event : events) {
            boolean isAssessment = event.getSource() == SourceKind.ASSESSMENT
                    || event.getEventId().startsWith("assessment-");
            if (previewType == PreviewType.ASSESSMENT && isAssessment) {
                filtered.add(event);
            } else if (previewType == PreviewType.SCHEDULE_EVENT && !isAssessment) {
                filtered.add(event);
            }
        }
        return filtered;
    }

    private String formatPreviewLine(ScheduleEvent event, ZoneId zoneId) {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(
                "yyyy-MM-dd HH:mm").withZone(zoneId);
        StringBuilder builder = new StringBuilder();
        builder.append(formatter.format(event.getStartsAt()))
                .append(" - ")
                .append(formatter.format(event.getEndsAt()))
                .append(" | ")
                .append(event.getTitle());
        if (event.getLocation() != null && !event.getLocation().isBlank()) {
            builder.append(" @ ").append(event.getLocation());
        }
        builder.append(" [").append(event.getSource().name()).append(']');
        return builder.toString();
    }

    private boolean withinWindow(Instant instant, Optional<Instant> windowStart,
                                 Optional<Instant> windowEnd) {
        if (instant == null) {
            return false;
        }
        if (windowStart.isPresent() && instant.isBefore(windowStart.get())) {
            return false;
        }
        return windowEnd.isEmpty() || !instant.isAfter(windowEnd.get());
    }

    @SafeVarargs
    private List<ScheduleEvent> composeEvents(List<ScheduleEvent>... sources) {
        LinkedHashMap<String, ScheduleEvent> merged = new LinkedHashMap<>();
        for (List<ScheduleEvent> source : sources) {
            if (source == null) {
                continue;
            }
            for (ScheduleEvent event : source) {
                if (event == null) {
                    continue;
                }
                merged.putIfAbsent(event.getEventId(), event);
            }
        }
        return new ArrayList<>(merged.values());
    }
}
