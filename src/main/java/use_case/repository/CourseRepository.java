package use_case.repository;

import entity.Course;
import java.util.List;
import java.util.Optional;

/**
 * Persistence boundary for course aggregates.
 */
public interface CourseRepository {
    Optional<Course> findById(String courseId);

    List<Course> findByUserId(String userId);

    void save(Course course);
}
