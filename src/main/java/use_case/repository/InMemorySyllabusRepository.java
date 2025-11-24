package use_case.repository;

import entity.Syllabus;
import use_case.repository.SyllabusRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemorySyllabusRepository implements SyllabusRepository {
    private final Map<String, Syllabus> syllabi = new HashMap<>();

    @Override
    public void save(Syllabus syllabus) {
        syllabi.put(syllabus.getSyllabusId(), syllabus);
        System.out.println("Saved syllabus: " + syllabus.getSyllabusId());
    }

}