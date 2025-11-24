package use_case.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Result of parsing a syllabus into structured data prior to domain mapping.
 */
public final class SyllabusParseResultData {
    private final String courseCode;
    private final String courseName;
    private final String term; 
    private final String instructor;
    private final List<AssessmentDraft> assessments;

    public SyllabusParseResultData(String courseCode,
                                   String courseName,
                                   String term,
                                   String instructor,
                                   List<AssessmentDraft> assessments) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.term = term;
        this.instructor = instructor;
        this.assessments = Collections.unmodifiableList(
                Objects.requireNonNull(assessments, "assessments"));
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getTerm() {
        return term;
    }

    public String getInstructor() {
        return instructor;
    }

    public List<AssessmentDraft> getAssessments() {
        return assessments;
    }

}
