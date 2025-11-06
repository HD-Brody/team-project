package domain.repository;

import domain.model.MarkingScheme;
import java.util.Optional;

/**
 * Persistence boundary for course marking schemes.
 */
public interface MarkingSchemeRepository {
    Optional<MarkingScheme> findByCourseId(String courseId);

    void save(MarkingScheme scheme);
}
