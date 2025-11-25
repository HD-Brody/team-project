package use_case.dto;

import entity.Assessment;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Input data for grade target calculations.
 */
public final class GradeCalculationRequest {
    private final String courseId;
    private final String userId;
    private final double targetPercent;
    private final List<Assessment> allAssessments;

    public GradeCalculationRequest(String courseId, String userId, double targetPercent,
                                   List<Assessment> allAssessments) {
        this.courseId = Objects.requireNonNull(courseId, "courseId");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.targetPercent = targetPercent;
        this.allAssessments = allAssessments;
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

    public List<Assessment> getAllAssessments() {
        return allAssessments;
    }
}
