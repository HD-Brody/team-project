package use_case.port.incoming;

import use_case.dto.CalendarExportRequest;
import use_case.dto.CalendarExportResponse;

/**
 * Generates external calendar artifacts for user tasks and assessments.
 */
public interface CalendarExportUseCase {
    CalendarExportResponse exportCalendar(CalendarExportRequest request);
}
