package use_case.port.outgoing;

/**
 * Extracts text from PDF syllabus files.
 */
public interface PdfExtractionDataAccessInterface {
    String extractText(String sourceFilePath);
}
