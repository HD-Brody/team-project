package application.port.outgoing;

import application.dto.SyllabusParseResult;

/**
 * Uses an AI provider to transform syllabus text into structured data.
 */
public interface AiExtractionPort {
    SyllabusParseResult extractStructuredData(String syllabusText);
}
