package domain.repository;

import domain.model.GradeEntry;
import java.util.List;

/**
 * Persistence boundary for grade entries.
 */
public interface GradeEntryRepository {
    List<GradeEntry> findByAssessmentId(String assessmentId);

    void save(GradeEntry gradeEntry);
}
