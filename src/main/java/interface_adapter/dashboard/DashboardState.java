package interface_adapter.dashboard;

import java.util.ArrayList;
import java.util.List;

public class DashboardState {
    private List<CourseDisplayData> courses = new ArrayList<>();
    private String error = null;

    public List<CourseDisplayData> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseDisplayData> courses) {
        this.courses = courses;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    /**
     * Data class for displaying course information in the dashboard.
     */
    public static class CourseDisplayData {
        private final String courseId;
        private final String courseCode;
        private final String courseName;
        private final List<AssessmentDisplayData> upcomingAssessments;

        public CourseDisplayData(String courseId, String courseCode, String courseName, 
                                List<AssessmentDisplayData> upcomingAssessments) {
            this.courseId = courseId;
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.upcomingAssessments = upcomingAssessments;
        }

        public String getCourseId() { return courseId; }
        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
        public List<AssessmentDisplayData> getUpcomingAssessments() { return upcomingAssessments; }
    }

    /**
     * Data class for displaying assessment information.
     */
    public static class AssessmentDisplayData {
        private final String title;
        private final String dueDate;

        public AssessmentDisplayData(String title, String dueDate) {
            this.title = title;
            this.dueDate = dueDate;
        }

        public String getTitle() { return title; }
        public String getDueDate() { return dueDate; }
    }
}