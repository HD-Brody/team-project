package use_case.service;

import use_case.dto.CalendarExportRequest;
import use_case.dto.CalendarExportResult;
import use_case.port.incoming.CalendarExportUseCase;
import use_case.port.outgoing.CalendarPublisherPort;
import use_case.repository.ScheduleEventRepository;
import use_case.repository.TaskRepository;
import java.util.Objects;

/**
 * Aggregates events and tasks before exporting them through a calendar gateway.
 */
public class CalendarExportService implements CalendarExportUseCase {
    private final ScheduleEventRepository scheduleEventRepository;
    private final TaskRepository taskRepository;
    private final CalendarPublisherPort calendarPublisherPort;

    public CalendarExportService(ScheduleEventRepository scheduleEventRepository,
                                 TaskRepository taskRepository,
                                 CalendarPublisherPort calendarPublisherPort) {
        this.scheduleEventRepository = Objects.requireNonNull(scheduleEventRepository,
                "scheduleEventRepository");
        this.taskRepository = Objects.requireNonNull(taskRepository, "taskRepository");
        this.calendarPublisherPort = Objects.requireNonNull(calendarPublisherPort,
                "calendarPublisherPort");
    }

    @Override
    public CalendarExportResult exportCalendar(CalendarExportRequest request) {
        // TODO: collect domain data and publish through the selected adapter.
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
