package use_case.dto;

import java.util.Arrays;
import java.util.Objects;

/**
 * Raw artifact contents returned by a calendar renderer.
 */
public final class CalendarRenderResult {
    private final byte[] payload;
    private final String filename;
    private final String contentType;

    public CalendarRenderResult(byte[] payload, String filename, String contentType) {
        this.payload = Arrays.copyOf(Objects.requireNonNull(payload, "payload"),
                payload.length);
        this.filename = Objects.requireNonNull(filename, "filename");
        this.contentType = Objects.requireNonNull(contentType, "contentType");
    }

    public byte[] getPayload() {
        return Arrays.copyOf(payload, payload.length);
    }

    public String getFilename() {
        return filename;
    }

    public String getContentType() {
        return contentType;
    }
}
