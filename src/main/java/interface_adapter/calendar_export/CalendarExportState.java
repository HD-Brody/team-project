package interface_adapter.calendar_export;

import java.util.ArrayList;
import java.util.List;

public class CalendarExportState {
    private List<String> courses = new ArrayList<>();
    private List<String> previewLines = new ArrayList<>();
    private String error;

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }

    public List<String> getPreviewLines() {
        return previewLines;
    }

    public void setPreviewLines(List<String> previewLines) {
        this.previewLines = previewLines;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
