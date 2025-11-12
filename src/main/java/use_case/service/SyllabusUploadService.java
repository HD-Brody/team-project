package use_case.service;

import use_case.dto.AssessmentDraft;
import use_case.dto.SyllabusParseResult;
import use_case.dto.UploadSyllabusData;
import use_case.dto.WeightComponentDraft;
import use_case.port.incoming.UploadSyllabusInputBoundary;
import use_case.port.outgoing.AiExtractionPort;
import use_case.port.outgoing.PdfExtractionPort;
import use_case.port.outgoing.SyllabusParsingPort;
import use_case.port.outgoing.TransactionalPersistencePort;
import use_case.repository.AssessmentRepository;
import use_case.repository.CourseRepository;
import use_case.repository.MarkingSchemeRepository;
import use_case.repository.SyllabusRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import entity.Assessment;
import entity.MarkingScheme;
import entity.Syllabus;
import entity.WeightComponent;

/**
 * Coordinates the ingestion pipeline from raw syllabus files to domain entities.
 */
public class SyllabusUploadService implements UploadSyllabusInputBoundary {
    private final PdfExtractionPort pdfExtractionPort;
    private final AiExtractionPort aiExtractionPort;
    // private final SyllabusParsingPort syllabusParsingPort; // Removed for simplification
    private final CourseRepository courseRepository;
    private final SyllabusRepository syllabusRepository;
    private final AssessmentRepository assessmentRepository;
    private final MarkingSchemeRepository markingSchemeRepository;
    // private final TransactionalPersistencePort transactionalPersistencePort; // Removed for simplification

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
        // this.syllabusParsingPort = Objects.requireNonNull(syllabusParsingPort, "syllabusParsingPort");
        this.courseRepository = Objects.requireNonNull(courseRepository, "courseRepository");
        this.syllabusRepository = Objects.requireNonNull(syllabusRepository, "syllabusRepository");
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository, "assessmentRepository");
        this.markingSchemeRepository = Objects.requireNonNull(markingSchemeRepository,
                "markingSchemeRepository");
        // this.transactionalPersistencePort = Objects.requireNonNull(transactionalPersistencePort,
        //         "transactionalPersistencePort");
    }

    @Override
    public void uploadSyllabus(UploadSyllabusData data) {
        // Extract raw text from the PDF syllabus file
        String rawText = pdfExtractionPort.extractText(data.getSourceFilePath());
        
        // Parse structured data using AI
        SyllabusParseResult parsedResult = aiExtractionPort.extractStructuredData(rawText);

        // Create entities
        String syllabusId = UUID.randomUUID().toString();
        Syllabus syllabus = new Syllabus(
                    syllabusId,
                    data.getCourseId(),
                    data.getSourceFilePath(),
                    Instant.now()
        );
        List<Assessment> assessments = draftAssessmentsToAssessments(syllabusId, parsedResult.getAssessments(), data.getCourseId());
        MarkingScheme markingScheme = mapMarkingScheme(syllabusId, parsedResult.getWeightComponents());

        // Persist entities using the repositories
        syllabusRepository.save(syllabus);
        assessmentRepository.saveAll(assessments);
        markingSchemeRepository.save(markingScheme);
    }

    private List<Assessment> draftAssessmentsToAssessments(String syllabusId, List<AssessmentDraft> drafts, String courseId) {
        List<Assessment> assessments = new ArrayList<>();
        for (AssessmentDraft draft : drafts) {

            Assessment assessment = new Assessment(
                    UUID.randomUUID().toString(),
                    courseId,
                    draft.getTitle(),
                    draft.getType(),
                    Instant.now().toString(),
                    draft.getDueDateIso(),
                    0L, // Placeholder for duration
                    draft.getWeight(),
                    draft.getSchemeComponentName(),
                    "TBD", // Placeholder for location
                    "" // Placeholder for notes
            );
            assessments.add(assessment);
        }
        return assessments;
    }

    private MarkingScheme mapMarkingScheme(String syllabusId, List<WeightComponentDraft> weightComponents) {
        // TODO: Implement mapping from weight components to marking scheme
        return new MarkingScheme(UUID.randomUUID().toString(), syllabusId, new ArrayList<>());
    }
}
    