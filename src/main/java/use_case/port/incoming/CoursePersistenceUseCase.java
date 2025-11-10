package use_case.port.incoming;

import use_case.dto.CourseSnapshot;

/**
 * Controls persistence and restoration of user course data.
 */
public interface CoursePersistenceUseCase {
    void persistCourseState(String userId);

    CourseSnapshot loadCourseState(String userId);
}
