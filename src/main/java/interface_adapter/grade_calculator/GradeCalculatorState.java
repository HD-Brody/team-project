package interface_adapter.grade_calculator;

import java.util.ArrayList;
import java.util.List;

public class GradeCalculatorState {
    private String courseId;
    private String courseName;
    private double targetPercent = 80.0;
    private List<AssessmentGradeData> assessments = new ArrayList<>();
    private CalculationResult result;
    private String error;

    public static class AssessmentGradeData {
        private final String assessmentId;
        private final String title;
        private final String type;
        private final double weight;
        private double grade; // -1 if not graded yet

        public AssessmentGradeData(String assessmentId, String title, String type, double weight, double grade) {
            this.assessmentId = assessmentId;
            this.title = title;
            this.type = type;
            this.weight = weight;
            this.grade = grade;
        }

        public String getAssessmentId() { return assessmentId; }
        public String getTitle() { return title; }
        public String getType() { return type; }
        public double getWeight() { return weight; }
        public double getGrade() { return grade; }
        public void setGrade(double grade) { this.grade = grade; }
    }

    public static class CalculationResult {
        private final double currentPercent;
        private final double bestCasePercent;
        private final double worstCasePercent;
        private final Double requiredAverageOnRemaining;
        private final List<RequiredScoreData> requiredScores;

        public CalculationResult(double currentPercent, double bestCasePercent, double worstCasePercent,
                               Double requiredAverageOnRemaining, List<RequiredScoreData> requiredScores) {
            this.currentPercent = currentPercent;
            this.bestCasePercent = bestCasePercent;
            this.worstCasePercent = worstCasePercent;
            this.requiredAverageOnRemaining = requiredAverageOnRemaining;
            this.requiredScores = requiredScores;
        }

        public double getCurrentPercent() { return currentPercent; }
        public double getBestCasePercent() { return bestCasePercent; }
        public double getWorstCasePercent() { return worstCasePercent; }
        public Double getRequiredAverageOnRemaining() { return requiredAverageOnRemaining; }
        public List<RequiredScoreData> getRequiredScores() { return requiredScores; }
    }

    public static class RequiredScoreData {
        private final String title;
        private final double requiredGrade;
        private final double weight;

        public RequiredScoreData(String title, double requiredGrade, double weight) {
            this.title = title;
            this.requiredGrade = requiredGrade;
            this.weight = weight;
        }

        public String getTitle() { return title; }
        public double getRequiredGrade() { return requiredGrade; }
        public double getWeight() { return weight; }
    }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public double getTargetPercent() { return targetPercent; }
    public void setTargetPercent(double targetPercent) { this.targetPercent = targetPercent; }

    public List<AssessmentGradeData> getAssessments() { return assessments; }
    public void setAssessments(List<AssessmentGradeData> assessments) { this.assessments = assessments; }

    public CalculationResult getResult() { return result; }
    public void setResult(CalculationResult result) { this.result = result; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
