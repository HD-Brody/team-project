package use_case.service;

import entity.Assessment;
import entity.AssessmentType;
import entity.Course;
import org.junit.jupiter.api.Test;
import use_case.dto.DashboardOutputData;
import use_case.port.outgoing.LoadDashboardOutputBoundary;
import use_case.repository.AssessmentRepository;
import use_case.repository.CourseRepository;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class LoadDashboardInteractorTest { // package-private to align with style

    // Success path covering all formatting branches
    @Test
    void execute_success_allBranches() {
        // Repositories
        CourseRepository courseRepo = new CourseRepository() {
            @Override
            public List<Course> findByUserId(String userId) { return Collections.singletonList(
                    new Course("C1", userId, "MAT101", "Calculus", null, null)); }
            @Override
            public void save(Course course) { /* no-op test stub */ }
        };
        AssessmentRepository assessmentRepo = new AssessmentRepository() {
            @Override
            public List<Assessment> findByCourseId(String courseId) {
                return Arrays.asList(
                        // Integer percentage path -> 20%
                        new Assessment("A1", courseId, "Midterm", AssessmentType.TEST, 0.0,
                                null, "2025-12-05T10:00:00Z", null, 0.20, null, null),
                        // Null date + null weight path -> TBD, ""
                        new Assessment("A2", courseId, "HW1", AssessmentType.ASSIGNMENT, 0.0,
                                null, null, null, null, null, null),
                        // Invalid date + regex trim path (12.50% -> 12.5%)
                        new Assessment("A3", courseId, "Quiz 1", AssessmentType.QUIZ, 0.0,
                                null, "not-a-date", null, 0.125, null, null),
                        // Valid date + fractional unchanged path (12.34%)
                        new Assessment("A4", courseId, "Project", AssessmentType.PROJECT, 0.0,
                                null, "2025-01-15T00:00:00Z", null, 0.1234, null, null),
                        // Empty date string path for isoDate.isEmpty()
                        new Assessment("A5", courseId, "Empty Date", AssessmentType.OTHER, 0.0,
                                null, "", null, null, null, null)
                );
            }
            @Override
            public void save(Assessment assessment) { /* no-op test stub */ }
        };

        // Presenter
        CapturingPresenter presenter = new CapturingPresenter();

        // Interactor
        LoadDashboardInteractor interactor = new LoadDashboardInteractor(courseRepo, assessmentRepo, presenter);
        interactor.execute("U1");

        assertNull(presenter.errorMessage);
        assertNotNull(presenter.outputData);
        assertEquals(1, presenter.outputData.getCourses().size());
        DashboardOutputData.CourseData courseData = presenter.outputData.getCourses().get(0);
        assertEquals("C1", courseData.getCourseId());
        assertEquals("MAT101", courseData.getCourseCode());
        assertEquals("Calculus", courseData.getCourseName());

        // Assessments mapped by title for easy assertions
        Map<String, DashboardOutputData.AssessmentData> byTitle = courseData.getUpcomingAssessments().stream()
                .collect(Collectors.toMap(DashboardOutputData.AssessmentData::getTitle, a -> a));
        assertEquals(5, byTitle.size());

        // Date formatting branches
        assertEquals("Dec. 5, 25", byTitle.get("Midterm").getDueDate());
        assertEquals("TBD", byTitle.get("HW1").getDueDate());
        assertEquals("not-a-date", byTitle.get("Quiz 1").getDueDate());
        assertEquals("Jan. 15, 25", byTitle.get("Project").getDueDate());
        assertEquals("TBD", byTitle.get("Empty Date").getDueDate()); // empty string path

        // Weight formatting branches
        assertEquals("20%", byTitle.get("Midterm").getWeight());
        assertEquals("", byTitle.get("HW1").getWeight());
        assertEquals("12.5%", byTitle.get("Quiz 1").getWeight());
        assertEquals("12.34%", byTitle.get("Project").getWeight());
        assertEquals("", byTitle.get("Empty Date").getWeight());

        // Types are enum names
        assertEquals("TEST", byTitle.get("Midterm").getType());
        assertEquals("ASSIGNMENT", byTitle.get("HW1").getType());
        assertEquals("QUIZ", byTitle.get("Quiz 1").getType());
        assertEquals("PROJECT", byTitle.get("Project").getType());
        assertEquals("OTHER", byTitle.get("Empty Date").getType());
    }

    // Error path test (repository throws -> presentError invoked)
    @Test
    void execute_error_repositoryThrows() {
        CourseRepository courseRepo = new CourseRepository() {
            @Override
            public List<Course> findByUserId(String userId) { throw new RuntimeException("DB down"); }
            @Override
            public void save(Course course) { /* no-op test stub */ }
        };
        AssessmentRepository assessmentRepo = new AssessmentRepository() {
            @Override
            public List<Assessment> findByCourseId(String courseId) { return Collections.emptyList(); }
            @Override
            public void save(Assessment assessment) { /* no-op test stub */ }
        };
        CapturingPresenter presenter = new CapturingPresenter();
        LoadDashboardInteractor interactor = new LoadDashboardInteractor(courseRepo, assessmentRepo, presenter);
        interactor.execute("U1");

        assertNull(presenter.outputData);
        assertNotNull(presenter.errorMessage);
        assertTrue(presenter.errorMessage.startsWith("Failed to load dashboard:"));
        assertTrue(presenter.errorMessage.contains("DB down"));
    }

    // Presenter double capturing outputs
    private static final class CapturingPresenter implements LoadDashboardOutputBoundary {
        DashboardOutputData outputData; String errorMessage;
        @Override public void presentDashboard(DashboardOutputData outputData) { this.outputData = outputData; }
        @Override public void presentError(String errorMessage) { this.errorMessage = errorMessage; }
    }
}
