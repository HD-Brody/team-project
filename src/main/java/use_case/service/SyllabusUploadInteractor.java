package use_case.service;

import use_case.dto.AssessmentDraft;
import use_case.dto.SyllabusParseResultData;
import use_case.dto.UploadSyllabusData;
import use_case.dto.WeightComponentDraft;
import use_case.port.incoming.UploadSyllabusInputBoundary;
import use_case.port.outgoing.AiExtractionDataAccessInterface;
import use_case.port.outgoing.PdfExtractionDataAccessInterface;
import use_case.port.outgoing.SyllabusParsingPort;
import use_case.port.outgoing.TransactionalPersistencePort;
import use_case.repository.AssessmentRepository;
import use_case.repository.CourseRepository;
import use_case.repository.MarkingSchemeRepository;
import use_case.repository.SyllabusRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class SyllabusUploadInteractor implements UploadSyllabusInputBoundary {
    private final PdfExtractionDataAccessInterface pdfExtractionPort;
    private final AiExtractionDataAccessInterface aiExtractionPort;
    // private final SyllabusParsingPort syllabusParsingPort; // Removed for simplification
    private final CourseRepository courseRepository;
    private final SyllabusRepository syllabusRepository;
    private final AssessmentRepository assessmentRepository;
    private final MarkingSchemeRepository markingSchemeRepository;
    // private final TransactionalPersistencePort transactionalPersistencePort; // Removed for simplification

    public SyllabusUploadInteractor(PdfExtractionDataAccessInterface pdfExtractionPort,
                                 AiExtractionDataAccessInterface aiExtractionPort,
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
        SyllabusParseResultData parsedResult = aiExtractionPort.extractStructuredData(rawText);

        // Create entities
        String syllabusId = UUID.randomUUID().toString();
        Syllabus syllabus = new Syllabus(
                    syllabusId,
                    data.getCourseId(),
                    data.getSourceFilePath(),
                    Instant.now()
        );
        MarkingScheme markingScheme = draftWeightsToMarkingScheme(syllabusId, parsedResult.getWeightComponents());

        List<Assessment> assessments = draftAssessmentsToAssessments(markingScheme, parsedResult.getAssessments(), data.getCourseId());

        // Persist entities using the repositories
        syllabusRepository.save(syllabus);
        markingSchemeRepository.save(markingScheme);
        assessmentRepository.saveAll(assessments);
    }

    private List<Assessment> draftAssessmentsToAssessments(MarkingScheme markingScheme, List<AssessmentDraft> drafts, String courseId) {
        // Create a map from Component Name to Component ID
        Map<String, String> componentNameToIdMap = markingScheme.getComponents().stream()
            .collect(Collectors.toMap(WeightComponent::getName, WeightComponent::getComponentId));

        List<Assessment> assessments = new ArrayList<>();
        for (AssessmentDraft draft : drafts) {
            // Use the map to find the correct component ID
            String componentId = componentNameToIdMap.get(draft.getSchemeComponentName());

            if (componentId == null) {
                System.err.println("Warning: Could not find component ID for name: '" + draft.getSchemeComponentName() + "'. Skipping assessment: '" + draft.getTitle() + "'");
                continue;
            }

            Assessment assessment = new Assessment(
                    UUID.randomUUID().toString(),
                    courseId,
                    draft.getTitle(),
                    draft.getType(),
                    null, // null for placeholder
                    draft.getDueDateIso(), // endsAt is the due date
                    0L,
                    draft.getWeight(),
                    componentId, 
                    "TBD", // location placeholder
                    "" // notes placeholder
            );
            assessments.add(assessment);
        }
        return assessments;
    }

    private MarkingScheme draftWeightsToMarkingScheme(String syllabusId, List<WeightComponentDraft> weightComponents) {
        List<WeightComponent> components = new ArrayList<>();
        
        String schemeId = UUID.randomUUID().toString();
        
        for (WeightComponentDraft draft : weightComponents) {
            WeightComponent component = new WeightComponent(
                UUID.randomUUID().toString(),   // componentId
                schemeId,                       // schemeId (links to the MarkingScheme)
                draft.getName(),                // name
                draft.getType(),                // type
                draft.getWeight(),              // weight
                draft.getExpectedCount()        // count
            );
            components.add(component);
        }
        
        return new MarkingScheme(schemeId, syllabusId, components);
    }
}
    