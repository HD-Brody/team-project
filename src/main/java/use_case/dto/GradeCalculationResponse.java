package use_case.dto;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Result of running the grade calculation use case.
 */
public final class GradeCalculationResponse {
    private final Map<String, Double> requiredScores;
    private final double projectedPercent;

    public GradeCalculationResponse(Map<String, Double> requiredScores, double projectedPercent) {
        this.requiredScores = Collections.unmodifiableMap(
                Objects.requireNonNull(requiredScores, "requiredScores"));
        this.projectedPercent = projectedPercent;
    }

    public Map<String, Double> getRequiredScores() {
        return requiredScores;
    }

    public double getProjectedPercent() {
        return projectedPercent;
    }
}
