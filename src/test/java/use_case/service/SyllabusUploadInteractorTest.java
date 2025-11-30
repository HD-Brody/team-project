package use_case.service;

import entity.Assessment;
import entity.AssessmentType;
import entity.Course;
import entity.Syllabus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.dto.AssessmentDraft;
import use_case.dto.SyllabusParseResultData;
import use_case.dto.SyllabusUploadInputData;
import use_case.dto.SyllabusUploadOutputData;
import use_case.port.outgoing.AiExtractionDataAccessInterface;
import use_case.port.outgoing.PdfExtractionDataAccessInterface;
import use_case.port.outgoing.SyllabusUploadOutputBoundary;
import use_case.repository.AssessmentRepository;
import use_case.repository.CourseRepository;
import use_case.repository.SyllabusRepository;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Minimal test suite providing 100% line coverage of SyllabusUploadInteractor.
 * Covers: constructor (via setup), success path (entity creation + loop), failure path (exception -> catch).
 */
public class SyllabusUploadInteractorTest {

    private MockPdfExtractor pdfExtractor;
    private MockAiExtractor aiExtractor;
    private MockCourseRepository courseRepository;
    private MockSyllabusRepository syllabusRepository;
    private MockAssessmentRepository assessmentRepository;
    private MockPresenter presenter;
    private SyllabusUploadInteractor interactor;

    @BeforeEach
    void setup() {
        pdfExtractor = new MockPdfExtractor();
        aiExtractor = new MockAiExtractor();
        courseRepository = new MockCourseRepository();
        syllabusRepository = new MockSyllabusRepository();
        assessmentRepository = new MockAssessmentRepository();
        presenter = new MockPresenter();

        interactor = new SyllabusUploadInteractor(
                pdfExtractor,
                aiExtractor,
                courseRepository,
                syllabusRepository,
                assessmentRepository,
                presenter
        );
    }

    @Test
    void testSuccessfulSyllabusUploadWithMultipleAssessments() {
        String userId = "user123";
        String filePath = "/path/to/syllabus.pdf";
        String rawText = "Sample syllabus text";

        List<AssessmentDraft> assessmentDrafts = Arrays.asList(
            new AssessmentDraft("Assignment 1", AssessmentType.ASSIGNMENT, "2025-01-15T23:59:00Z", 20.0),
            new AssessmentDraft("Midterm Exam", AssessmentType.EXAM, "2025-02-20T14:00:00Z", 30.0),
            new AssessmentDraft("Final Exam", AssessmentType.EXAM, "2025-04-15T09:00:00Z", 50.0)
        );

        SyllabusParseResultData parseResult = new SyllabusParseResultData(
            "CSC207",
            "Software Design",
            "Fall 2025",
            "Prof. Shorser",
            assessmentDrafts
        );

        pdfExtractor.setMockText(rawText);
        aiExtractor.setMockResult(parseResult);

        SyllabusUploadInputData inputData = new SyllabusUploadInputData(userId, filePath);
        interactor.execute(inputData);

        assertTrue(presenter.isSuccess());
        assertNotNull(presenter.getOutputData());
        assertEquals("Software Design", presenter.getOutputData().getCourseName());
        assertEquals(3, presenter.getOutputData().getAssessmentCount());

        assertEquals(1, courseRepository.getSavedCourses().size());
        assertEquals(1, syllabusRepository.getSavedSyllabi().size());
        assertEquals(3, assessmentRepository.getSavedAssessments().size());

        Course savedCourse = courseRepository.getSavedCourses().get(0);
        assertEquals(userId, savedCourse.getUserId());
        assertEquals("CSC207", savedCourse.getCode());
        assertEquals("Software Design", savedCourse.getName());
        assertEquals("Fall 2025", savedCourse.getTerm());
        assertEquals("Prof. Shorser", savedCourse.getInstructor());

        Syllabus savedSyllabus = syllabusRepository.getSavedSyllabi().get(0);
        assertEquals(savedCourse.getCourseId(), savedSyllabus.getCourseId());
        assertEquals(filePath, savedSyllabus.getSourceFilePath());

        List<Assessment> savedAssessments = assessmentRepository.getSavedAssessments();
        assertEquals(3, savedAssessments.size());
    }

    @Test
    void testFailureWhenPdfExtractionThrowsException() {
        String userId = "user999";
        String filePath = "/bad/file.pdf";

        pdfExtractor.setShouldThrowException(true);

        SyllabusUploadInputData inputData = new SyllabusUploadInputData(userId, filePath);
        interactor.execute(inputData);

        assertFalse(presenter.isSuccess());
        assertNotNull(presenter.getErrorMessage());
        assertTrue(presenter.getErrorMessage().startsWith("Failed to process syllabus:"));
        assertTrue(presenter.getErrorMessage().contains("PDF extraction failed"));

        assertEquals(0, courseRepository.getSavedCourses().size());
        assertEquals(0, syllabusRepository.getSavedSyllabi().size());
        assertEquals(0, assessmentRepository.getSavedAssessments().size());
    }

    // ---- Mocks (trimmed to what is needed by the minimal tests) ----

    private static class MockPdfExtractor implements PdfExtractionDataAccessInterface {
        private String mockText = "";
        private boolean shouldThrowException = false;
        public void setMockText(String text) { this.mockText = text; }
        public void setShouldThrowException(boolean shouldThrow) { this.shouldThrowException = shouldThrow; }
        @Override
        public String extractText(String filePath) {
            if (shouldThrowException) { throw new RuntimeException("PDF extraction failed"); }
            return mockText;
        }
    }

    private static class MockAiExtractor implements AiExtractionDataAccessInterface {
        private SyllabusParseResultData mockResult;
        @Override
        public SyllabusParseResultData extractStructuredData(String syllabusText) { return mockResult; }
        public void setMockResult(SyllabusParseResultData result) { this.mockResult = result; }
    }

    private static class MockCourseRepository implements CourseRepository {
        private final List<Course> savedCourses = new ArrayList<>();
        public List<Course> getSavedCourses() { return savedCourses; }
        @Override
        public void save(Course course) { savedCourses.add(course); }
        @Override
        public List<Course> findByUserId(String userId) { return new ArrayList<>(); }
    }

    private static class MockSyllabusRepository implements SyllabusRepository {
        private final List<Syllabus> savedSyllabi = new ArrayList<>();
        public List<Syllabus> getSavedSyllabi() { return savedSyllabi; }
        @Override
        public void save(Syllabus syllabus) { savedSyllabi.add(syllabus); }
        @Override
        public List<Syllabus> findSyllabusByCourseID(String courseID) { return new ArrayList<>(); }
    }

    private static class MockAssessmentRepository implements AssessmentRepository {
        private final List<Assessment> savedAssessments = new ArrayList<>();
        public List<Assessment> getSavedAssessments() { return savedAssessments; }
        @Override
        public void save(Assessment assessment) { savedAssessments.add(assessment); }
        @Override
        public List<Assessment> findByCourseId(String courseId) { return new ArrayList<>(); }
        @Override
        public java.util.Optional<Assessment> findById(String id) { return java.util.Optional.empty(); }
        @Override
        public void update(Assessment assessment) { /* no-op */ }
        @Override
        public void deleteById(String id) { /* no-op */ }
    }

    private static class MockPresenter implements SyllabusUploadOutputBoundary {
        private boolean success = false;
        private SyllabusUploadOutputData outputData;
        private String errorMessage;
        public boolean isSuccess() { return success; }
        public SyllabusUploadOutputData getOutputData() { return outputData; }
        public String getErrorMessage() { return errorMessage; }
        @Override
        public void prepareSuccessView(SyllabusUploadOutputData data) { this.success = true; this.outputData = data; }
        @Override
        public void prepareFailView(String error) { this.success = false; this.errorMessage = error; }
    }
}

