package data_access;

import data_access.ai.gemini.AiExtractorDataAccessObject;
import data_access.parser.pdf.PdfExtractorDataAccessObject;

import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import use_case.dto.SyllabusParseResultData;

class GeminiTest {

    @Test
    void testPdfToGemini() {
        // Extract text from PDF
        PdfExtractorDataAccessObject pdfExtractor = new PdfExtractorDataAccessObject();
        String pdfPath = "src/test/resources/sample-syllabus-MAT237.pdf";
        String extractedText = pdfExtractor.extractText(pdfPath);

        System.out.println("=== EXTRACTED TEXT ===");
        System.out.println(extractedText);
        System.out.println("\n=== END TEXT ===\n");

        // Load API key from config.properties
        Properties config = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            config.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String apiKey = config.getProperty("gemini.api.key");    

        // Call Gemini to extract structured data
        AiExtractorDataAccessObject aiExtractor = new AiExtractorDataAccessObject(apiKey);
        SyllabusParseResultData result = aiExtractor.extractStructuredData(extractedText);

        // Print what Gemini returned
        System.out.println("=== GEMINI RESPONSE ===");
        System.out.println("\n--- Course Information ---");
        System.out.println("Course Code: " + result.getCourseCode());
        System.out.println("Course Name: " + result.getCourseName());
        System.out.println("Term: " + result.getTerm());
        System.out.println("Instructor: " + result.getInstructor());
        System.out.println("Assessments found: " + result.getAssessments().size());
        System.out.println("\n--- Assessments ---");
        result.getAssessments().forEach(assessment -> {
            System.out.println("• " + assessment.getTitle());
            System.out.println("  Type: " + assessment.getType());
            System.out.println("  Weight: " + (assessment.getWeight() * 100) + "%");
            System.out.println("  Due: " + assessment.getDueDateIso());
            System.out.println();
        });

    }
}
