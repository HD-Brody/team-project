package interface_adapter.grade;

import entity.Assessment;
import entity.Course;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import use_case.dto.GradeCalculationResponse;

/**
 * Maps grade calculation responses to the view model.
 */
public class GradeCalculationPresenter {
    private final GradeCalculationViewModel viewModel;

    public GradeCalculationPresenter(GradeCalculationViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void presentCourses(String userId, List<Course> courses) {
        GradeCalculationState state = viewModel.getState();
        state.setUserId(userId);
        state.setCourses(courses.stream()
                .map(c -> new GradeCalculationState.CourseOption(c.getCourseId(),
                        c.getCode() + " - " + c.getName()))
                .collect(Collectors.toList()));
        state.setErrorMessage(null);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    public void presentCalculation(GradeCalculationResponse response, List<Assessment> allAssessments,
                                   String userId, String courseId, double targetPercent) {
        GradeCalculationState state = viewModel.getState();
        state.setUserId(userId);
        state.setCourseId(courseId);
        state.setTargetPercent(targetPercent);
        state.setProjectedPercent(response.getProjectedPercent());
        state.setRequiredScores(buildRows(response.getRequiredScores(), allAssessments));
        state.setErrorMessage(null);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    public void presentError(String message) {
        GradeCalculationState state = viewModel.getState();
        state.setErrorMessage(message);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    private List<GradeCalculationState.RequiredScoreRow> buildRows(List<Assessment> requiredScores,
                                                                   List<Assessment> allAssessments) {
        Map<String, Assessment> originals = new HashMap<>();
        if (allAssessments != null) {
            for (Assessment assessment : allAssessments) {
                originals.put(assessment.getAssessmentId(), assessment);
            }
        }

        List<GradeCalculationState.RequiredScoreRow> rows = new ArrayList<>();
        if (requiredScores != null) {
            for (Assessment required : requiredScores) {
                Assessment original = originals.get(required.getAssessmentId());
                Double currentGrade = original != null ? original.getGrade() : null;
                Double requiredGrade = required.getGrade();
                double weight = required.getWeight() != null ? required.getWeight() * 100 : 0.0;
                rows.add(new GradeCalculationState.RequiredScoreRow(
                        required.getAssessmentId(),
                        required.getTitle(),
                        required.getType().toString(),
                        weight,
                        currentGrade,
                        requiredGrade
                ));
            }
        }
        return rows;
    }
}
