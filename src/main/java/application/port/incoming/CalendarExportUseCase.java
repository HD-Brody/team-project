package application.port.incoming;

import application.dto.CalendarExportRequest;
import application.dto.CalendarExportResult;

/**
 * Generates external calendar artifacts for user tasks and assessments.
 */
public interface CalendarExportUseCase {
    CalendarExportResult exportCalendar(CalendarExportRequest request);
}
