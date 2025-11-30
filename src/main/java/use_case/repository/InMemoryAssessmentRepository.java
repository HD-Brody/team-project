package use_case.repository;

import entity.Assessment;
import use_case.repository.AssessmentRepository;

import java.util.*;

public class InMemoryAssessmentRepository implements AssessmentRepository {
    private final Map<String, Assessment> assessments = new HashMap<>();

    @Override
    public List<Assessment> findByCourseId(String courseId) {
        List<Assessment> result = new ArrayList<>();
        for (Assessment assessment : assessments.values()) {
            if (assessment.getCourseId().equals(courseId)) {
                result.add(assessment);
            }
        }
        return result;
    }

    @Override
    public void save(Assessment assessment) {
        assessments.put(assessment.getAssessmentId(), assessment);
        System.out.println("Saved assessment: " + assessment.getTitle() + 
                           " (Type: " + assessment.getType() + 
                           ", Weight: " + (assessment.getWeight() * 100) + "%)");
    }

    @Override
    public Optional<Assessment> findById(String assessmentId) {
        return Optional.ofNullable(assessments.get(assessmentId));
    }

    @Override
    public void update(Assessment assessment) {
        assessments.put(assessment.getAssessmentId(), assessment);
        System.out.println("Updated assessment: " + assessment.getTitle());
    }

    @Override
    public void deleteById(String assessmentId) {
        Assessment removed = assessments.remove(assessmentId);
        if (removed != null) {
            System.out.println("Deleted assessment: " + removed.getTitle());
        }
    }

}