package use_case.service;

import use_case.dto.AssessmentDraft;
import use_case.dto.SyllabusParseResultData;
import use_case.dto.UploadSyllabusData;
import use_case.port.incoming.UploadSyllabusInputBoundary;
import use_case.port.outgoing.AiExtractionDataAccessInterface;
import use_case.port.outgoing.PdfExtractionDataAccessInterface;
import use_case.repository.AssessmentRepository;
import use_case.repository.CourseRepository;
import use_case.repository.SyllabusRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import entity.Assessment;
import entity.Course;
import entity.MarkingScheme;
import entity.Syllabus;
import entity.WeightComponent;

/**
 * Coordinates the ingestion pipeline from raw syllabus files to domain entities.
 */
public class SyllabusUploadInteractor implements UploadSyllabusInputBoundary {
    private final PdfExtractionDataAccessInterface pdfExtractionPort;
    private final AiExtractionDataAccessInterface aiExtractionPort;
    private final CourseRepository courseRepository;
    private final SyllabusRepository syllabusRepository;
    private final AssessmentRepository assessmentRepository;

    public SyllabusUploadInteractor(PdfExtractionDataAccessInterface pdfExtractionPort,
                                 AiExtractionDataAccessInterface aiExtractionPort,
                                 CourseRepository courseRepository,
                                 SyllabusRepository syllabusRepository,
                                 AssessmentRepository assessmentRepository) {
        this.pdfExtractionPort = Objects.requireNonNull(pdfExtractionPort, "pdfExtractionPort");
        this.aiExtractionPort = Objects.requireNonNull(aiExtractionPort, "aiExtractionPort");
        this.courseRepository = Objects.requireNonNull(courseRepository, "courseRepository");
        this.syllabusRepository = Objects.requireNonNull(syllabusRepository, "syllabusRepository");
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository, "assessmentRepository");
    }

    @Override
    public void execute(UploadSyllabusData data) {
        // Extract raw text from the PDF syllabus file
        String rawText = pdfExtractionPort.extractText(data.getSourceFilePath());
        
        // Parse structured data using AI
        SyllabusParseResultData parsedResult = aiExtractionPort.extractStructuredData(rawText);

        // Create entities
        String courseId = UUID.randomUUID().toString();
        Course course = new Course(
            courseId,
            data.getUserId(),
            parsedResult.getCourseCode(),
            parsedResult.getCourseName(),
            parsedResult.getTerm(),
            parsedResult.getInstructor()
        );

        String syllabusId = UUID.randomUUID().toString();
        Syllabus syllabus = new Syllabus(
                    syllabusId,
                    courseId,
                    data.getSourceFilePath()
        );
        List<Assessment> assessments = draftAssessmentsToAssessments(parsedResult.getAssessments(), courseId);

        // Persist entities using the repositories
        courseRepository.save(course);
        syllabusRepository.save(syllabus);
        for (Assessment a : assessments) {
            assessmentRepository.save(a);
        }
    }

    private List<Assessment> draftAssessmentsToAssessments(List<AssessmentDraft> drafts, String courseId) {
        // Create a map from Component Name to Component ID
        List<Assessment> assessments = new ArrayList<>();
        for (AssessmentDraft draft : drafts) {
            Assessment assessment = new Assessment(
                    UUID.randomUUID().toString(),
                    courseId,
                    draft.getTitle(),
                    draft.getType(),
                    -1.0, // grade placeholder
                    null, // null for placeholder
                    draft.getDueDateIso(), // endsAt is the due date
                    0L,
                    draft.getWeight(),
                    "", // location placeholder
                    "" // notes placeholder
            );
            assessments.add(assessment);
        }
        return assessments;
    }

}
    