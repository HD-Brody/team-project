package use_case.port.outgoing;

import use_case.dto.SyllabusParseResult;

/**
 * Parses raw syllabus resources into structured data.
 */
public interface SyllabusParsingPort {
    SyllabusParseResult parse(String sourceFilePath);
}
