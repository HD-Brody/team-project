package interface_adapter.task_list;

import java.util.ArrayList;
import java.util.List;

public class TaskListState {
    private String courseId = "";
    private String courseName = "";
    private List<TaskData> tasks = new ArrayList<>();
    private String error = null;
    
    public TaskListState() {
    }
    
    public TaskListState(TaskListState copy) {
        this.courseId = copy.courseId;
        this.courseName = copy.courseName;
        this.tasks = new ArrayList<>(copy.tasks);
        this.error = copy.error;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<TaskData> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskData> tasks) {
        this.tasks = tasks;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    
    public static class TaskData {
        private final String assessmentId;
        private final String title;
        private final String dueDate;
        private final String status;
        private final String notes;
        private final Integer durationMinutes;
        
        public TaskData(String assessmentId, String title, String dueDate, 
                       String status, String notes, Integer durationMinutes) {
            this.assessmentId = assessmentId;
            this.title = title;
            this.dueDate = dueDate;
            this.status = status;
            this.notes = notes;
            this.durationMinutes = durationMinutes;
        }

        public String getAssessmentId() {
            return assessmentId;
        }

        public String getTitle() {
            return title;
        }

        public String getDueDate() {
            return dueDate;
        }

        public String getStatus() {
            return status;
        }

        public String getNotes() {
            return notes;
        }

        public Integer getDurationMinutes() {
            return durationMinutes;
        }
    }
}
