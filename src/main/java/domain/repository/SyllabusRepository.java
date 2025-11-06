package domain.repository;

import domain.model.Syllabus;
import java.util.Optional;

/**
 * Persistence boundary for syllabus metadata.
 */
public interface SyllabusRepository {
    Optional<Syllabus> findActiveByCourseId(String courseId);

    void save(Syllabus syllabus);
}
