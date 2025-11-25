package entity;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents an assessment defined by the course syllabus.
 */
public final class Assessment {
    private final String assessmentId;
    private final String courseId;
    private final String title;
    private final AssessmentType type;
    private final double grade;
    private final String startsAt;
    private final String endsAt;
    private final Long durationMinutes;
    private final Double weight;
    private final String location;
    private final String notes;

    public Assessment(String assessmentId, String courseId, String title, AssessmentType type, double grade,
                      String startsAt, String endsAt, Long durationMinutes, Double weight, String location, String notes) {
        this.assessmentId = Objects.requireNonNull(assessmentId, "assessmentId");
        this.courseId = Objects.requireNonNull(courseId, "courseId");
        this.title = Objects.requireNonNull(title, "title");
        this.type = Objects.requireNonNull(type, "type");
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.durationMinutes = durationMinutes;
        this.weight = weight;
        this.location = location;
        this.notes = notes;
        this.grade = grade;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public AssessmentType getType() {
        return type;
    }

    public double getGrade() {
        return grade;
    }

    public String getStartsAt() {
        return startsAt;
    }

    public String getEndsAt() {
        return endsAt;
    }

    public Long getDurationMinutes() {
        return durationMinutes;
    }

    public Double getWeight() {
        return weight;
    }

    public String getLocation() {
        return location;
    }

    public String getNotes() {
        return notes;
    }

}
