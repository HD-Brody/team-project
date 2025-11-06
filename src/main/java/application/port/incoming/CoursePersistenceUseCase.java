package application.port.incoming;

import application.dto.CourseSnapshot;

/**
 * Controls persistence and restoration of user course data.
 */
public interface CoursePersistenceUseCase {
    void persistCourseState(String userId);

    CourseSnapshot loadCourseState(String userId);
}
