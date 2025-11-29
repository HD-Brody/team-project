package use_case.port.outgoing;

import use_case.dto.CalendarExportResponse;

public interface CalendarExportOutputPort {
    void presentExport(CalendarExportResponse response);
    void presentError(String errorMessage);
}
