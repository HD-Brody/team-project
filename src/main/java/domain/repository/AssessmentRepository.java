package domain.repository;

import domain.model.Assessment;
import java.util.List;
import java.util.Optional;

/**
 * Persistence boundary for assessments.
 */
public interface AssessmentRepository {
    Optional<Assessment> findById(String assessmentId);

    List<Assessment> findByCourseId(String courseId);

    void saveAll(List<Assessment> assessments);
}
