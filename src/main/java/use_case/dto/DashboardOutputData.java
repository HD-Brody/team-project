package use_case.dto;

import java.util.List;

public class DashboardOutputData {
    private final List<CourseData> courses;

    public DashboardOutputData(List<CourseData> courses) {
        this.courses = courses;
    }

    public List<CourseData> getCourses() {
        return courses;
    }

    public static class CourseData {
        private final String courseId;
        private final String courseCode;
        private final String courseName;
        private final List<AssessmentData> upcomingAssessments;

        public CourseData(String courseId, String courseCode, String courseName, 
                         List<AssessmentData> upcomingAssessments) {
            this.courseId = courseId;
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.upcomingAssessments = upcomingAssessments;
        }

        public String getCourseId() { return courseId; }
        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
        public List<AssessmentData> getUpcomingAssessments() { return upcomingAssessments; }
    }

    public static class AssessmentData {
        private final String title;
        private final String dueDate;
        private final String type;
        private final String weight;

        public AssessmentData(String title, String dueDate, String type, String weight) {
            this.title = title;
            this.dueDate = dueDate;
            this.type = type;
            this.weight = weight;
        }

        public String getTitle() { return title; }
        public String getDueDate() { return dueDate; }
        public String getType() { return type; }
        public String getWeight() { return weight; }
    }
}