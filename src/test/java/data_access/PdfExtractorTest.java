package data_access;

import org.junit.jupiter.api.Test;
import data_access.parser.pdf.PdfExtractorDataAccessObject;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PdfExtractorTest {
    @Test
    void extractText_validPdf_returnsText() throws IOException {
        PdfExtractorDataAccessObject extractor = new PdfExtractorDataAccessObject();
        
        String samplePdfPath = "src/test/resources/sample-syllabus-STA237.pdf";
        
        String result = extractor.extractText(samplePdfPath);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.length() > 0);
        
        // Print for manual verification
        System.out.println("Extracted: " + result.substring(0, Math.min(10000, result.length())) + "...");
    }
}
