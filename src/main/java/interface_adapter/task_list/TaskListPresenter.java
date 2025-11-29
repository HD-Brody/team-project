package interface_adapter.task_list;

import entity.Assessment;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class TaskListPresenter {
    public final TaskListViewModel viewModel;

    public TaskListPresenter(TaskListViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void presentTasks(String courseId, String courseName, List<Assessment> assessments) {
        TaskListState state = viewModel.getState();
        state.setCourseId(courseId);
        state.setCourseName(courseName);
        
        List<TaskListState.TaskData> taskDataList = assessments.stream()
            .map(this::convertToTaskData)
            .collect(Collectors.toList());
        
        state.setTasks(taskDataList);
        state.setError(null);
        
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    public void presentError(String errorMessage) {
        TaskListState state = viewModel.getState();
        state.setError(errorMessage);
        state.setTasks(List.of());
        
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    public void presentTaskCreated() {
        // Trigger reload by firing property change
        viewModel.firePropertyChange();
    }

    public void presentTaskDeleted() {
        // Trigger reload by firing property change
        viewModel.firePropertyChange();
    }

    private TaskListState.TaskData convertToTaskData(Assessment assessment) {
        String formattedDate = formatDate(assessment.getEndsAt());
        String status = extractStatusFromNotes(assessment.getNotes());
        String cleanNotes = extractNotesWithoutStatus(assessment.getNotes());
        
        return new TaskListState.TaskData(
            assessment.getAssessmentId(),
            assessment.getTitle(),
            formattedDate,
            status,
            cleanNotes,
            assessment.getDurationMinutes() != null ? assessment.getDurationMinutes().intValue() : null
        );
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty() || "null".equals(isoDate)) {
            return "No due date";
        }
        try {
            // Try parsing as ISO instant first
            Instant instant = Instant.parse(isoDate);
            return DateTimeFormatter.ofPattern("MMM d, yyyy")
                .withZone(ZoneId.systemDefault())
                .format(instant);
        } catch (Exception e) {
            // Try parsing as date only
            try {
                return isoDate.substring(0, 10); // Just return YYYY-MM-DD
            } catch (Exception ex) {
                return isoDate;
            }
        }
    }

    private String extractStatusFromNotes(String notes) {
        if (notes == null || !notes.contains("[Status:")) {
            return "TODO";
        }
        try {
            int start = notes.indexOf("[Status: ") + 9;
            int end = notes.indexOf("]", start);
            if (start > 8 && end > start) {
                return notes.substring(start, end);
            }
        } catch (Exception e) {
            // Fall through
        }
        return "TODO";
    }

    private String extractNotesWithoutStatus(String notes) {
        if (notes == null || !notes.contains("[Status:")) {
            return notes != null ? notes : "";
        }
        int statusLineEnd = notes.indexOf("\n");
        if (statusLineEnd > 0 && statusLineEnd < notes.length() - 1) {
            return notes.substring(statusLineEnd + 1);
        }
        return "";
    }
}
