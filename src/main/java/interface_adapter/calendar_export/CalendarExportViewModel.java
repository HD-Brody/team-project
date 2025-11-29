package interface_adapter.calendar_export;

import interface_adapter.ViewModel;

public class CalendarExportViewModel extends ViewModel<CalendarExportState> {
    public CalendarExportViewModel() {
        super("calendar export");
        setState(new CalendarExportState());
    }
}
