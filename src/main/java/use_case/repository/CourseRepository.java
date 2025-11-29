package use_case.repository;

import entity.Course;
import java.util.List;
import java.util.Optional;

/**
 * Persistence boundary for course aggregates.
 */
public interface CourseRepository {
    public List<Course> findByUserId(String userId);
    public void save(Course course);
}
