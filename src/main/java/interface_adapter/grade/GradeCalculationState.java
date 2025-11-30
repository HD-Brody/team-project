package interface_adapter.grade;

import java.util.ArrayList;
import java.util.List;

/**
 * View-facing state for grade calculation UI.
 */
public class GradeCalculationState {
    private String userId;
    private String courseId;
    private final List<CourseOption> courses = new ArrayList<>();
    private double targetPercent = 80.0;
    private Double projectedPercent;
    private final List<RequiredScoreRow> requiredScores = new ArrayList<>();
    private String errorMessage;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public List<CourseOption> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseOption> options) {
        courses.clear();
        if (options != null) {
            courses.addAll(options);
        }
    }

    public double getTargetPercent() {
        return targetPercent;
    }

    public void setTargetPercent(double targetPercent) {
        this.targetPercent = targetPercent;
    }

    public Double getProjectedPercent() {
        return projectedPercent;
    }

    public void setProjectedPercent(Double projectedPercent) {
        this.projectedPercent = projectedPercent;
    }

    public List<RequiredScoreRow> getRequiredScores() {
        return requiredScores;
    }

    public void setRequiredScores(List<RequiredScoreRow> rows) {
        requiredScores.clear();
        if (rows != null) {
            requiredScores.addAll(rows);
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static final class CourseOption {
        private final String courseId;
        private final String displayName;

        public CourseOption(String courseId, String displayName) {
            this.courseId = courseId;
            this.displayName = displayName;
        }

        public String getCourseId() {
            return courseId;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static final class RequiredScoreRow {
        private final String assessmentId;
        private final String assessmentTitle;
        private final String assessmentType;
        private final double weightPercent;
        private final Double currentGrade;
        private final Double requiredGrade;

        public RequiredScoreRow(String assessmentId, String assessmentTitle, String assessmentType,
                                double weightPercent, Double currentGrade, Double requiredGrade) {
            this.assessmentId = assessmentId;
            this.assessmentTitle = assessmentTitle;
            this.assessmentType = assessmentType;
            this.weightPercent = weightPercent;
            this.currentGrade = currentGrade;
            this.requiredGrade = requiredGrade;
        }

        public String getAssessmentId() {
            return assessmentId;
        }

        public String getAssessmentTitle() {
            return assessmentTitle;
        }

        public String getAssessmentType() {
            return assessmentType;
        }

        public double getWeightPercent() {
            return weightPercent;
        }

        public Double getCurrentGrade() {
            return currentGrade;
        }

        public Double getRequiredGrade() {
            return requiredGrade;
        }
    }
}
