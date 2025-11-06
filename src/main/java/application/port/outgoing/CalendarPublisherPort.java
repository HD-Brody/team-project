package application.port.outgoing;

import application.dto.CalendarExportRequest;
import application.dto.CalendarExportResult;

/**
 * Writes calendar outputs to external systems (ICS files, Google Calendar, etc.).
 */
public interface CalendarPublisherPort {
    CalendarExportResult publish(CalendarExportRequest request);
}
