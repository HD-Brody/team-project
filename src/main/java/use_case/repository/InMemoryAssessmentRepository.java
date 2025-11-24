package use_case.repository;

import entity.Assessment;
import use_case.repository.AssessmentRepository;

import java.util.*;

public class InMemoryAssessmentRepository implements AssessmentRepository {
    private final Map<String, Assessment> assessments = new HashMap<>();

    @Override
    public void save(Assessment assessment) {
        assessments.put(assessment.getAssessmentId(), assessment);
        System.out.println("Saved assessment: " + assessment.getTitle() + 
                           " (Type: " + assessment.getType() + 
                           ", Weight: " + (assessment.getWeight() * 100) + "%)");
    }

}