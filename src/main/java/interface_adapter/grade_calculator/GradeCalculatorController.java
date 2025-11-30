package interface_adapter.grade_calculator;

import entity.Assessment;
import use_case.dto.GradeCalculationRequest;
import use_case.dto.GradeCalculationResponse;
import use_case.port.incoming.GradeCalculationUseCase;
import use_case.repository.AssessmentRepository;
import use_case.repository.CourseRepository;
import use_case.repository.SessionRepository;

import java.util.List;

public class GradeCalculatorController {
    private final GradeCalculationUseCase gradeCalculationUseCase;
    private final AssessmentRepository assessmentRepository;
    private final CourseRepository courseRepository;
    private final SessionRepository sessionRepository;
    private final GradeCalculatorPresenter presenter;

    public GradeCalculatorController(GradeCalculationUseCase gradeCalculationUseCase,
                                    AssessmentRepository assessmentRepository,
                                    CourseRepository courseRepository,
                                    SessionRepository sessionRepository,
                                    GradeCalculatorPresenter presenter) {
        this.gradeCalculationUseCase = gradeCalculationUseCase;
        this.assessmentRepository = assessmentRepository;
        this.courseRepository = courseRepository;
        this.sessionRepository = sessionRepository;
        this.presenter = presenter;
    }

    public void loadCourse(String courseId) {
        try {
            String userId = getUserId();
            
            // Get course name
            entity.Course course = courseRepository.findByUserId(userId).stream()
                .filter(c -> c.getCourseId().equals(courseId))
                .findFirst()
                .orElse(null);
            
            String courseName = course != null ? course.getCode() : courseId;
            
            // Load assessments
            List<Assessment> assessments = assessmentRepository.findByCourseId(courseId);
            
            presenter.presentAssessments(courseId, courseName, assessments);
        } catch (Exception e) {
            presenter.presentError("Failed to load course: " + e.getMessage());
        }
    }

    public void calculateGrades(String courseId, double targetPercent) {
        try {
            String userId = getUserId();
            
            // Load all assessments for the course
            List<Assessment> assessments = assessmentRepository.findByCourseId(courseId);
            
            // Create request
            GradeCalculationRequest request = new GradeCalculationRequest(
                courseId, userId, targetPercent, assessments
            );
            
            // Calculate
            GradeCalculationResponse response = gradeCalculationUseCase.calculateTargets(request);
            
            presenter.presentCalculationResult(response);
        } catch (Exception e) {
            presenter.presentError("Failed to calculate grades: " + e.getMessage());
        }
    }

    public void updateAssessmentGrade(String courseId, String assessmentId, Double grade) {
        try {
            // Find and update the assessment
            Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found"));
            
            // Create updated assessment with new grade (use -1 if null to indicate not graded)
            Assessment updated = new Assessment(
                assessment.getAssessmentId(),
                assessment.getCourseId(),
                assessment.getTitle(),
                assessment.getType(),
                grade != null ? grade : -1.0,
                assessment.getStartsAt(),
                assessment.getEndsAt(),
                assessment.getDurationMinutes(),
                assessment.getWeight(),
                assessment.getLocation(),
                assessment.getNotes()
            );
            
            assessmentRepository.update(updated);
            
            // Reload course to reflect changes
            loadCourse(courseId);
        } catch (Exception e) {
            presenter.presentError("Failed to update grade: " + e.getMessage());
        }
    }

    private String getUserId() {
        entity.Session session = sessionRepository.getSession();
        return session != null ? session.getUserID() : "defaultUser";
    }
}
