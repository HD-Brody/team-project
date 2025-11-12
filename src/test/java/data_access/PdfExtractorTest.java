package data_access;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import data_access.parser.pdf.PdfBoxPdfExtractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class PdfExtractorTest {
    @Test
    void extractText_validPdf_returnsText(@TempDir Path tempDir) throws IOException {
        PdfBoxPdfExtractor extractor = new PdfBoxPdfExtractor();
        
        String samplePdfPath = "src/test/resources/sample-syllabus.pdf";
        
        String result = extractor.extractText(samplePdfPath);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.length() > 0);
        
        // Print for manual verification
        System.out.println("Extracted: " + result.substring(0, Math.min(200, result.length())) + "...");
    }
}
