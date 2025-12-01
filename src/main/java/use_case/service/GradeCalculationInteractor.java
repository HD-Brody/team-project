package use_case.service;

import entity.Assessment;
import entity.AssessmentType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import use_case.dto.GradeCalculationInputData;
import use_case.dto.GradeCalculationOutputData;
import use_case.port.incoming.GradeCalculationUseCase;


/**
 * Provides weighted grade projections for a course.
 */
public class GradeCalculationInteractor implements GradeCalculationUseCase {
    private final List<Assessment> allAssessments;

    public GradeCalculationInteractor(List<Assessment> allAssessments) {
        this.allAssessments = allAssessments;
    }

    @Override
    public GradeCalculationOutputData calculateTargets(GradeCalculationInputData request) {
        Objects.requireNonNull(request, "GradeCalculationRequest must not be null");
        String courseID = request.getCourseId();
        double targetPercent = request.getTargetPercent();

        List<Assessment> assessments = request.getAllAssessments() != null
                ? request.getAllAssessments()
                : (allAssessments != null ? allAssessments : List.of());
        List<Assessment> courseAssessments = new ArrayList<>();
        for (Assessment assessment : assessments) {
            if (courseID.equals(assessment.getCourseId())) {
                courseAssessments.add(assessment);
            }
        }

        double totalWeight = 0.0;
        double completedWeight = 0.0;
        double completedWeightedScore = 0.0;
        List<Assessment> remainingAssessments = new ArrayList<>();

        for (Assessment assessment : courseAssessments) {
            double weight = assessment.getWeight() != null ? assessment.getWeight() : 0.0;
            totalWeight += weight;

            double grade = assessment.getGrade();
            if (grade >= 0) {
                completedWeight += weight;
                completedWeightedScore += weight * grade;
            } else {
                remainingAssessments.add(assessment);
            }
        }

        double remainingWeight = totalWeight - completedWeight;
        double currentPercent = completedWeight > 0 ? completedWeightedScore / completedWeight : 0.0;
        double worstCasePercent = totalWeight > 0 ? completedWeightedScore / totalWeight : 0.0;
        double bestCasePercent = totalWeight > 0
                ? (completedWeightedScore + remainingWeight * 100.0) / totalWeight
                : 0.0;

        Double requiredAverageOnRemaining = null;
        if (targetPercent > 0 && remainingWeight > 0 && totalWeight > 0) {
            requiredAverageOnRemaining = (targetPercent * totalWeight - completedWeightedScore) / remainingWeight;
        }

        List<Assessment> requiredScores = new ArrayList<>();
        requiredScores.add(buildSummaryAssessment("best-case-" + courseID, courseID, "BEST_CASE_PERCENT",
                bestCasePercent, totalWeight));
        requiredScores.add(buildSummaryAssessment("worst-case-" + courseID, courseID, "WORST_CASE_PERCENT",
                worstCasePercent, totalWeight));
        requiredScores.add(buildSummaryAssessment("current-" + courseID, courseID, "CURRENT_PERCENT",
                currentPercent, completedWeight));

        if (requiredAverageOnRemaining != null) {
            requiredScores.add(buildSummaryAssessment("required-average-" + courseID, courseID,
                    "REQUIRED_AVERAGE_REMAINING", requiredAverageOnRemaining, remainingWeight));
            for (Assessment remaining : remainingAssessments) {
                requiredScores.add(cloneWithGrade(remaining, requiredAverageOnRemaining));
            }
        }

        return new GradeCalculationOutputData(requiredScores, currentPercent);
    }

    private Assessment buildSummaryAssessment(String assessmentId, String courseId, String title,
                                              double grade, Double weight) {
        return new Assessment(
                assessmentId,
                courseId,
                title,
                AssessmentType.OTHER,
                grade,
                null,
                null,
                null,
                weight,
                null,
                null
        );
    }

    private Assessment cloneWithGrade(Assessment assessment, double grade) {
        return new Assessment(
                assessment.getAssessmentId(),
                assessment.getCourseId(),
                assessment.getTitle(),
                assessment.getType(),
                grade,
                assessment.getStartsAt(),
                assessment.getEndsAt(),
                assessment.getDurationMinutes(),
                assessment.getWeight(),
                assessment.getLocation(),
                assessment.getNotes()
        );
    }
}
