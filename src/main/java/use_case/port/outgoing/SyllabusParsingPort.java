package use_case.port.outgoing;

import use_case.dto.SyllabusParseResultData;

/**
 * Parses raw syllabus resources into structured data.
 */
public interface SyllabusParsingPort {
    SyllabusParseResultData parse(String sourceFilePath);
}
