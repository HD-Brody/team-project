package use_case.repository;

import entity.Syllabus;
import use_case.repository.SyllabusRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemorySyllabusRepository implements SyllabusRepository {
    private final Map<String, Syllabus> syllabi = new HashMap<>();

    @Override
    public List<Syllabus> findSyllabusByCourseID(String syllabusId) {
        return syllabi.get(syllabusId) != null ? List.of(syllabi.get(syllabusId)) : List.of();
    }

    @Override
    public void save(Syllabus syllabus) {
        syllabi.put(syllabus.getSyllabusId(), syllabus);
        System.out.println("Saved syllabus: " + syllabus.getSyllabusId());
    }

}