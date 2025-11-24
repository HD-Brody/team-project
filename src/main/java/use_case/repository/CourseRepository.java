package use_case.repository;

import entity.Course;
import java.util.List;
import java.util.Optional;

/**
 * Persistence boundary for course aggregates.
 */
public interface CourseRepository {
    void save(Course course);
}
