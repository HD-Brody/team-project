package data_access.parser.pdf;

import use_case.port.outgoing.PdfExtractionDataAccessInterface;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PdfExtractorDataAccessObject implements PdfExtractionDataAccessInterface {
    @Override
    public String extractText(String sourceFilePath) {
        File file = new File(sourceFilePath);
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract text from: " + sourceFilePath, e);
        }
    }
}