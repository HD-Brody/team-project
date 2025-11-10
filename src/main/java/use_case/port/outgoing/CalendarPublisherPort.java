package use_case.port.outgoing;

import use_case.dto.CalendarExportRequest;
import use_case.dto.CalendarExportResult;

/**
 * Writes calendar outputs to external systems (ICS files, Google Calendar, etc.).
 */
public interface CalendarPublisherPort {
    CalendarExportResult publish(CalendarExportRequest request);
}
