package use_case.repository;

import entity.Assessment;
import java.util.List;
import java.util.Optional;

/**
 * Persistence boundary for assessments.
 */
public interface AssessmentRepository {
    public List<Assessment> findByCourseID(String courseID);
    void save(Assessment assessment);
}
