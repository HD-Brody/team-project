package application.service;

import application.dto.CalendarExportRequest;
import application.dto.CalendarExportResult;
import application.port.incoming.CalendarExportUseCase;
import application.port.outgoing.CalendarPublisherPort;
import domain.repository.ScheduleEventRepository;
import domain.repository.TaskRepository;
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
