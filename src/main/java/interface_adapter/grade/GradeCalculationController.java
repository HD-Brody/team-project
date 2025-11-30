package interface_adapter.grade;

import entity.Assessment;
import entity.Course;
import java.util.List;
import use_case.dto.GradeCalculationRequest;
import use_case.dto.GradeCalculationResponse;
import use_case.port.incoming.GradeCalculationUseCase;
import use_case.repository.AssessmentRepository;
import use_case.repository.CourseRepository;
import use_case.repository.SessionRepository;

/**
 * Controller for grade calculation interactions.
 */
public class GradeCalculationController {
    private final GradeCalculationUseCase gradeCalculationUseCase;
    private final CourseRepository courseRepository;
    private final AssessmentRepository assessmentRepository;
    private final SessionRepository sessionRepository;
    private final GradeCalculationPresenter presenter;

    public GradeCalculationController(GradeCalculationUseCase gradeCalculationUseCase,
                                      CourseRepository courseRepository,
                                      AssessmentRepository assessmentRepository,
                                      SessionRepository sessionRepository,
                                      GradeCalculationPresenter presenter) {
        this.gradeCalculationUseCase = gradeCalculationUseCase;
        this.courseRepository = courseRepository;
        this.assessmentRepository = assessmentRepository;
        this.sessionRepository = sessionRepository;
        this.presenter = presenter;
    }

    public void loadCourses(String providedUserId) {
        try {
            String userId = resolveUserId(providedUserId);
            List<Course> courses = courseRepository.findByUserId(userId);
            presenter.presentCourses(userId, courses);
        } catch (Exception e) {
            presenter.presentError("Unable to load courses: " + e.getMessage());
        }
    }

    public void calculateTargets(String providedUserId, String courseId, double targetPercent) {
        try {
            String userId = resolveUserId(providedUserId);
            List<Assessment> assessments = assessmentRepository.findByCourseId(courseId);
            GradeCalculationRequest request = new GradeCalculationRequest(courseId, userId, targetPercent, assessments);
            GradeCalculationResponse response = gradeCalculationUseCase.calculateTargets(request);
            presenter.presentCalculation(response, assessments, userId, courseId, targetPercent);
        } catch (Exception e) {
            presenter.presentError("Unable to calculate targets: " + e.getMessage());
        }
    }

    public void presentInputError(String message) {
        presenter.presentError(message);
    }

    private String resolveUserId(String providedUserId) {
        if (providedUserId != null && !providedUserId.isBlank()) {
            return providedUserId;
        }
        entity.Session session = sessionRepository.getSession();
        if (session != null && session.getUserID() != null) {
            return session.getUserID();
        }
        return "defaultUser";
    }
}
