package application.port.outgoing;

import application.dto.SyllabusParseResult;

/**
 * Parses raw syllabus resources into structured data.
 */
public interface SyllabusParsingPort {
    SyllabusParseResult parse(String sourceFilePath);
}
