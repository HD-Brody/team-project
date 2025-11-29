package interface_adapter.task_list;

import entity.Assessment;
import entity.AssessmentType;
import entity.TaskStatus;
import use_case.dto.TaskCreationCommand;
import use_case.port.incoming.TaskEditingUseCase;
import use_case.repository.AssessmentRepository;
import use_case.repository.CourseRepository;
import use_case.repository.SessionRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskListController {
    private final TaskEditingUseCase taskEditingUseCase;
    private final AssessmentRepository assessmentRepository;
    private final CourseRepository courseRepository;
    private final SessionRepository sessionRepository;
    private final TaskListPresenter presenter;

    public TaskListController(TaskEditingUseCase taskEditingUseCase,
                             AssessmentRepository assessmentRepository,
                             CourseRepository courseRepository,
                             SessionRepository sessionRepository,
                             TaskListPresenter presenter) {
        this.taskEditingUseCase = taskEditingUseCase;
        this.assessmentRepository = assessmentRepository;
        this.courseRepository = courseRepository;
        this.sessionRepository = sessionRepository;
        this.presenter = presenter;
    }

    public void loadTasks(String courseId) {
        try {
            // Get course name
            entity.Course course = courseRepository.findByUserId(getUserId()).stream()
                .filter(c -> c.getCourseId().equals(courseId))
                .findFirst()
                .orElse(null);
            
            String courseName = course != null ? course.getCode() + " - " + course.getName() : courseId;
            
            // Load assessments for this course
            List<Assessment> assessments = assessmentRepository.findByCourseId(courseId);
            
            presenter.presentTasks(courseId, courseName, assessments);
        } catch (Exception e) {
            presenter.presentError("Failed to load tasks: " + e.getMessage());
        }
    }

    public void createTask(String title, String dueDate, Integer durationMinutes, 
                          TaskStatus status, String notes) {
        try {
            String courseId = presenter.viewModel.getState().getCourseId();
            
            TaskCreationCommand command = new TaskCreationCommand(
                getUserId(),
                courseId,
                null, // assessmentId - null for user-created tasks
                title,
                parseDate(dueDate),
                durationMinutes,
                null, // priority - not used in Assessment
                status,
                notes
            );
            
            taskEditingUseCase.createTask(command);
            presenter.presentTaskCreated();
            
            // Reload tasks
            loadTasks(courseId);
        } catch (Exception e) {
            presenter.presentError("Failed to create task: " + e.getMessage());
        }
    }

    public void deleteTask(String assessmentId) {
        try {
            taskEditingUseCase.deleteTask(assessmentId);
            presenter.presentTaskDeleted();
            
            // Reload tasks
            String courseId = presenter.viewModel.getState().getCourseId();
            loadTasks(courseId);
        } catch (UnsupportedOperationException e) {
            presenter.presentError("Delete functionality not yet implemented in database");
        } catch (Exception e) {
            presenter.presentError("Failed to delete task: " + e.getMessage());
        }
    }

    private String getUserId() {
        entity.Session session = sessionRepository.getSession();
        return session != null ? session.getUserID() : "defaultUser";
    }

    private Instant parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            // Parse as YYYY-MM-DD and set to end of day
            return java.time.LocalDate.parse(dateStr)
                .atTime(23, 59, 0)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        } catch (Exception e) {
            return null;
        }
    }
}
