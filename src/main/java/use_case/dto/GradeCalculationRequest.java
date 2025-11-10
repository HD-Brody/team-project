package use_case.dto;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Input data for grade target calculations.
 */
public final class GradeCalculationRequest {
    private final String courseId;
    private final String userId;
    private final double targetPercent;
    private final Map<String, Double> anticipatedScores;

    public GradeCalculationRequest(String courseId, String userId, double targetPercent,
                                   Map<String, Double> anticipatedScores) {
        this.courseId = Objects.requireNonNull(courseId, "courseId");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.targetPercent = targetPercent;
        this.anticipatedScores = Collections.unmodifiableMap(
                Objects.requireNonNull(anticipatedScores, "anticipatedScores"));
    }

    public String getCourseId() {
        return courseId;
    }

    public String getUserId() {
        return userId;
    }

    public double getTargetPercent() {
        return targetPercent;
    }

    public Map<String, Double> getAnticipatedScores() {
        return anticipatedScores;
    }
}
