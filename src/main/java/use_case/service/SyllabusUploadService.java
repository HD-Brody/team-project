package use_case.service;

import use_case.dto.UploadSyllabusCommand;
import use_case.port.incoming.UploadSyllabusUseCase;
import use_case.port.outgoing.AiExtractionPort;
import use_case.port.outgoing.PdfExtractionPort;
import use_case.port.outgoing.SyllabusParsingPort;
import use_case.port.outgoing.TransactionalPersistencePort;
import use_case.repository.AssessmentRepository;
import use_case.repository.CourseRepository;
import use_case.repository.MarkingSchemeRepository;
import use_case.repository.SyllabusRepository;
import java.util.Objects;

/**
 * Coordinates the ingestion pipeline from raw syllabus files to domain entities.
 */
public class SyllabusUploadService implements UploadSyllabusUseCase {
    private final PdfExtractionPort pdfExtractionPort;
    private final AiExtractionPort aiExtractionPort;
    private final SyllabusParsingPort syllabusParsingPort;
    private final CourseRepository courseRepository;
    private final SyllabusRepository syllabusRepository;
    private final AssessmentRepository assessmentRepository;
    private final MarkingSchemeRepository markingSchemeRepository;
    private final TransactionalPersistencePort transactionalPersistencePort;

    public SyllabusUploadService(PdfExtractionPort pdfExtractionPort,
                                 AiExtractionPort aiExtractionPort,
                                 SyllabusParsingPort syllabusParsingPort,
                                 CourseRepository courseRepository,
                                 SyllabusRepository syllabusRepository,
                                 AssessmentRepository assessmentRepository,
                                 MarkingSchemeRepository markingSchemeRepository,
                                 TransactionalPersistencePort transactionalPersistencePort) {
        this.pdfExtractionPort = Objects.requireNonNull(pdfExtractionPort, "pdfExtractionPort");
        this.aiExtractionPort = Objects.requireNonNull(aiExtractionPort, "aiExtractionPort");
        this.syllabusParsingPort = Objects.requireNonNull(syllabusParsingPort, "syllabusParsingPort");
        this.courseRepository = Objects.requireNonNull(courseRepository, "courseRepository");
        this.syllabusRepository = Objects.requireNonNull(syllabusRepository, "syllabusRepository");
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository, "assessmentRepository");
        this.markingSchemeRepository = Objects.requireNonNull(markingSchemeRepository,
                "markingSchemeRepository");
        this.transactionalPersistencePort = Objects.requireNonNull(transactionalPersistencePort,
                "transactionalPersistencePort");
    }

    @Override
    public void uploadSyllabus(UploadSyllabusCommand command) {
        // TODO: implement parsing pipeline and repository persistence.
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
