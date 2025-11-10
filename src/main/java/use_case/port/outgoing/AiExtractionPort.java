package use_case.port.outgoing;

import use_case.dto.SyllabusParseResult;

/**
 * Uses an AI provider to transform syllabus text into structured data.
 */
public interface AiExtractionPort {
    SyllabusParseResult extractStructuredData(String syllabusText);
}
