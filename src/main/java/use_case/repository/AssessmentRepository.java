package use_case.repository;

import entity.Assessment;
import java.util.List;
import java.util.Optional;

/**
 * Persistence boundary for assessments.
 */
public interface AssessmentRepository {


    void save(Assessment assessments);
}
