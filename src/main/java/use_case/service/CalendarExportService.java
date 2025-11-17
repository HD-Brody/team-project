package use_case.service;

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
import use_case.dto.CalendarExportRequest;
import use_case.dto.CalendarExportResponse;
import use_case.dto.CalendarRenderRequest;
import use_case.dto.CalendarRenderResult;
import use_case.dto.ScheduleEventSnapshot;
import use_case.dto.ScheduledTaskSnapshot;
import use_case.port.incoming.CalendarExportUseCase;
import use_case.port.outgoing.CalendarRenderPort;
import use_case.port.outgoing.ScheduleEventQueryPort;
import use_case.port.outgoing.ScheduledTaskQueryPort;

/**
 * Aggregates events and tasks before exporting them through a calendar renderer.
 */
public class CalendarExportService implements CalendarExportUseCase {
    private static final String DEFAULT_PRODUCT_ID = "-//MARBLE//Calendar Export//EN";
    private static final Duration DEFAULT_TASK_DURATION = Duration.ofHours(1);

    private final ScheduledTaskQueryPort scheduledTaskQueryPort;
    private final ScheduleEventQueryPort scheduleEventQueryPort;
    private final CalendarRenderPort calendarRenderPort;

    public CalendarExportService(ScheduledTaskQueryPort scheduledTaskQueryPort,
                                 ScheduleEventQueryPort scheduleEventQueryPort,
                                 CalendarRenderPort calendarRenderPort) {
        this.scheduledTaskQueryPort = Objects.requireNonNull(scheduledTaskQueryPort,
                "scheduledTaskQueryPort");
        this.scheduleEventQueryPort = Objects.requireNonNull(scheduleEventQueryPort,
                "scheduleEventQueryPort");
        this.calendarRenderPort = Objects.requireNonNull(calendarRenderPort,
                "calendarRenderPort");
    }

    @Override
    public CalendarExportResponse exportCalendar(CalendarExportRequest request) {
        Objects.requireNonNull(request, "request");

        ZoneId zoneId = parseZone(request.getTimezoneId());
        List<ScheduleEvent> events = composeEvents(request.getEvents());

        if (events.isEmpty()) {
            events = composeEvents(
                    tasksToEvents(loadTasks(request), request.getUserId()),
                    snapshotsToEvents(loadScheduleEvents(request))
            );
        }

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

    private ZoneId parseZone(String timezoneId) {
        try {
            return ZoneId.of(timezoneId);
        } catch (DateTimeException ex) {
            throw new IllegalArgumentException("Unsupported timezone: " + timezoneId, ex);
        }
    }

    private List<ScheduledTaskSnapshot> loadTasks(CalendarExportRequest request) {
        return scheduledTaskQueryPort.findTasksForExport(
                request.getUserId(),
                request.getCourseIds(),
                request.getWindowStart(),
                request.getWindowEnd()
        );
    }

    private List<ScheduleEventSnapshot> loadScheduleEvents(CalendarExportRequest request) {
        return scheduleEventQueryPort.findScheduleEvents(
                request.getUserId(),
                request.getCourseIds(),
                request.getWindowStart(),
                request.getWindowEnd()
        );
    }

    private List<ScheduleEvent> tasksToEvents(List<ScheduledTaskSnapshot> snapshots,
                                              String userId) {
        List<ScheduleEvent> events = new ArrayList<>();
        for (ScheduledTaskSnapshot snapshot : snapshots) {
            Instant dueAt = snapshot.getDueAt();
            if (dueAt == null) {
                continue;
            }

            Instant endsAt = dueAt.plus(DEFAULT_TASK_DURATION);
            String notes = enrichNotesWithWeight(snapshot.getNotes(),
                    snapshot.getWeightPercent());

            events.add(new ScheduleEvent(
                    "task-" + snapshot.getTaskId(),
                    userId,
                    snapshot.getTitle(),
                    dueAt,
                    endsAt,
                    snapshot.getLocation(),
                    notes,
                    SourceKind.TASK,
                    snapshot.getTaskId()
            ));
        }
        return events;
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

    private List<ScheduleEvent> snapshotsToEvents(List<ScheduleEventSnapshot> snapshots) {
        List<ScheduleEvent> events = new ArrayList<>();
        for (ScheduleEventSnapshot snapshot : snapshots) {
            events.add(new ScheduleEvent(
                    snapshot.getEventId(),
                    snapshot.getUserId(),
                    snapshot.getTitle(),
                    snapshot.getStartsAt(),
                    snapshot.getEndsAt(),
                    snapshot.getLocation(),
                    snapshot.getNotes(),
                    snapshot.getSource(),
                    snapshot.getSourceId()
            ));
        }
        return events;
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
