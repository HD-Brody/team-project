package use_case.dto;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

/**
 * Finalized artifact metadata for a calendar export.
 */
public final class CalendarExportResponse {
    private final byte[] payload;
    private final String filename;
    private final String mediaType;
    private final int eventCount;
    private final Instant generatedAt;

    public CalendarExportResponse(byte[] payload, String filename, String mediaType,
                                  int eventCount, Instant generatedAt) {
        this.payload = Arrays.copyOf(Objects.requireNonNull(payload, "payload"),
                payload.length);
        this.filename = Objects.requireNonNull(filename, "filename");
        this.mediaType = Objects.requireNonNull(mediaType, "mediaType");
        this.eventCount = eventCount;
        this.generatedAt = Objects.requireNonNull(generatedAt, "generatedAt");
    }

    public byte[] getPayload() {
        return Arrays.copyOf(payload, payload.length);
    }

    public String getFilename() {
        return filename;
    }

    public String getMediaType() {
        return mediaType;
    }

    public int getEventCount() {
        return eventCount;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }
}
