package use_case.dto;

import entity.Assessment;

import java.util.List;

/**
 * Result of running the grade calculation use case.
 */
public final class GradeCalculationOutputData {
    private final List<Assessment> requiredScores;
    private final double projectedPercent;

    public GradeCalculationOutputData(List<Assessment> requiredScores, double projectedPercent) {
        this.requiredScores = requiredScores;
        this.projectedPercent = projectedPercent;
    }

    public List<Assessment> getRequiredScores() {
        return requiredScores;
    }

    public double getProjectedPercent() {
        return projectedPercent;
    }
}
