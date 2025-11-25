package use_case.dto;

import entity.Assessment;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.List;

/**
 * Result of running the grade calculation use case.
 */
public final class GradeCalculationResponse {
    private final List<Assessment> requiredScores;
    private final double projectedPercent;

    public GradeCalculationResponse(List<Assessment> requiredScores, double projectedPercent) {
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
