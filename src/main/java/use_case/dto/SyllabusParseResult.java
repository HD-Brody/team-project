package use_case.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Result of parsing a syllabus into structured data prior to domain mapping.
 */
public final class SyllabusParseResult {
    private final String courseCode;
    private final String courseName;
    private final List<AssessmentDraft> assessments;
    private final List<WeightComponentDraft> weightComponents;
    private final List<String> warnings;

    public SyllabusParseResult(String courseCode, String courseName,
                               List<AssessmentDraft> assessments,
                               List<WeightComponentDraft> weightComponents,
                               List<String> warnings) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.assessments = Collections.unmodifiableList(
                Objects.requireNonNull(assessments, "assessments"));
        this.weightComponents = Collections.unmodifiableList(
                Objects.requireNonNull(weightComponents, "weightComponents"));
        this.warnings = Collections.unmodifiableList(
                Objects.requireNonNull(warnings, "warnings"));
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public List<AssessmentDraft> getAssessments() {
        return assessments;
    }

    public List<WeightComponentDraft> getWeightComponents() {
        return weightComponents;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}
