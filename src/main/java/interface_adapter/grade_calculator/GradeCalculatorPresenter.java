package interface_adapter.grade_calculator;

import entity.Assessment;
import use_case.dto.GradeCalculationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GradeCalculatorPresenter {
    private final GradeCalculatorViewModel viewModel;

    public GradeCalculatorPresenter(GradeCalculatorViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void presentAssessments(String courseId, String courseName, List<Assessment> assessments) {
        GradeCalculatorState state = viewModel.getState();
        state.setCourseId(courseId);
        state.setCourseName(courseName);
        
        List<GradeCalculatorState.AssessmentGradeData> assessmentData = assessments.stream()
            .map(a -> new GradeCalculatorState.AssessmentGradeData(
                a.getAssessmentId(),
                a.getTitle(),
                a.getType().toString(),
                a.getWeight() != null ? a.getWeight() : 0.0,
                a.getGrade()
            ))
            .collect(Collectors.toList());
        
        state.setAssessments(assessmentData);
        state.setError(null);
        
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    public void presentCalculationResult(GradeCalculationResponse response) {
        GradeCalculatorState state = viewModel.getState();
        
        // Extract summary values from response
        double currentPercent = response.getProjectedPercent();
        double bestCase = 0.0;
        double worstCase = 0.0;
        Double requiredAvg = null;
        List<GradeCalculatorState.RequiredScoreData> requiredScores = new ArrayList<>();
        
        for (Assessment assessment : response.getRequiredScores()) {
            String title = assessment.getTitle();
            double grade = assessment.getGrade();
            double weight = assessment.getWeight() != null ? assessment.getWeight() : 0.0;
            
            if ("BEST_CASE_PERCENT".equals(title)) {
                bestCase = grade;
            } else if ("WORST_CASE_PERCENT".equals(title)) {
                worstCase = grade;
            } else if ("CURRENT_PERCENT".equals(title)) {
                currentPercent = grade;
            } else if ("REQUIRED_AVERAGE_REMAINING".equals(title)) {
                requiredAvg = grade;
            } else {
                // Individual assessment required scores
                requiredScores.add(new GradeCalculatorState.RequiredScoreData(title, grade, weight));
            }
        }
        
        GradeCalculatorState.CalculationResult result = new GradeCalculatorState.CalculationResult(
            currentPercent, bestCase, worstCase, requiredAvg, requiredScores
        );
        
        state.setResult(result);
        state.setError(null);
        
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    public void presentError(String errorMessage) {
        GradeCalculatorState state = viewModel.getState();
        state.setError(errorMessage);
        state.setResult(null);
        
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }
}
