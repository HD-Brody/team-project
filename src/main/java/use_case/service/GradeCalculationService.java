package use_case.service;

import use_case.dto.GradeCalculationRequest;
import use_case.dto.GradeCalculationResponse;
import use_case.port.incoming.GradeCalculationUseCase;
import use_case.repository.AssessmentRepository;
import use_case.repository.GradeEntryRepository;
import use_case.repository.MarkingSchemeRepository;
import entity.Assessment;
import java.util.Objects;
import use_case.port.outgoing.AssignmentListPort;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.ArrayList;


/**
 * Provides weighted grade projections for a course.
 */
public class GradeCalculationService implements GradeCalculationUseCase {
    private final List<Assessment> allAssessments;

    public GradeCalculationService(List<Assessment> allAssessments) {
        this.allAssessments = allAssessments;
    }

    @Override
    public GradeCalculationResponse calculateTargets(GradeCalculationRequest request) {
        // TODO: implement weighted grade calculation logic.
        Objects.requireNonNull(request, "GradeCalculationRequest must not be null");
        String userID = request.getUserId();
        String courseID = request.getCourseId();
        double targetPercent =  request.getTargetPercent();
        List<Assessment> assessments = allAssessments;


        // return new (GradeCalculationResponse
        // requiredScores
        // projectedPercent);
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
