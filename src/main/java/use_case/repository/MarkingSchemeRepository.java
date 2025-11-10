package use_case.repository;

import entity.MarkingScheme;
import java.util.Optional;

/**
 * Persistence boundary for course marking schemes.
 */
public interface MarkingSchemeRepository {
    Optional<MarkingScheme> findByCourseId(String courseId);

    void save(MarkingScheme scheme);
}
