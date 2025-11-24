package use_case.port.outgoing;

import use_case.dto.SyllabusParseResultData;

/**
 * Uses an AI provider to transform syllabus text into structured data.
 */
public interface AiExtractionDataAccessInterface {
    SyllabusParseResultData extractStructuredData(String syllabusText);
}
