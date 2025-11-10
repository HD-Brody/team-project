package use_case.dto;

import java.util.Objects;

/**
 * Outcome of exporting a calendar.
 */
public final class CalendarExportResult {
    private final String artifactPath;
    private final int eventCount;

    public CalendarExportResult(String artifactPath, int eventCount) {
        this.artifactPath = Objects.requireNonNull(artifactPath, "artifactPath");
        this.eventCount = eventCount;
    }

    public String getArtifactPath() {
        return artifactPath;
    }

    public int getEventCount() {
        return eventCount;
    }
}
