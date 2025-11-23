package use_case.repository;

import entity.Syllabus;

import java.util.List;
import java.util.Optional;

/**
 * Persistence boundary for syllabus metadata.
 */
public interface SyllabusRepository {
    public List<Syllabus> findSyllabusByCourseID(String courseID);
    void save(Syllabus syllabus);
}
