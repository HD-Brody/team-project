package entity;

import java.time.Instant;
import java.util.Objects;

/**
 * Stores a graded result for an assessment submission.
 */
public final class GradeEntry {
    private final String gradeEntryId;
    private final String assessmentId;
    private final Double pointsEarned;
    private final Double pointsPossible;
    private final Double percent;
    private final Instant gradedAt;
    private final String feedback;

    public GradeEntry(String gradeEntryId, String assessmentId, Double pointsEarned,
                      Double pointsPossible, Double percent, Instant gradedAt, String feedback) {
        this.gradeEntryId = Objects.requireNonNull(gradeEntryId, "gradeEntryId");
        this.assessmentId = Objects.requireNonNull(assessmentId, "assessmentId");
        this.pointsEarned = pointsEarned;
        this.pointsPossible = pointsPossible;
        this.percent = percent;
        this.gradedAt = gradedAt;
        this.feedback = feedback;
    }

    public String getGradeEntryId() {
        return gradeEntryId;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public Double getPointsEarned() {
        return pointsEarned;
    }

    public Double getPointsPossible() {
        return pointsPossible;
    }

    public Double getPercent() {
        return percent;
    }

    public Instant getGradedAt() {
        return gradedAt;
    }

    public String getFeedback() {
        return feedback;
    }
}
