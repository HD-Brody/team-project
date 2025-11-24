package use_case.dto;

public class SyllabusUploadOutputData {
    private final String courseName;
    private final int assessmentCount;

    public SyllabusUploadOutputData(String courseName, int assessmentCount) {
        this.courseName = courseName;
        this.assessmentCount = assessmentCount;
    }

    public String getCourseName() { return courseName; }
    public int getAssessmentCount() { return assessmentCount; }
}