package use_case.dto;

import entity.Assessment;

import java.util.List;
import java.util.Objects;

/**
 * Input data for grade target calculations.
 */
public final class GradeCalculationInputData {
    private final String courseId;
    private final String userId;
    private final double targetPercent;
    private final List<Assessment> allAssessments;

    public GradeCalculationInputData(String courseId, String userId, double targetPercent,
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
