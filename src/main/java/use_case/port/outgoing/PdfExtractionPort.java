package use_case.port.outgoing;

/**
 * Extracts text from PDF syllabus files.
 */
public interface PdfExtractionPort {
    String extractPlainText(String sourceFilePath);
}
