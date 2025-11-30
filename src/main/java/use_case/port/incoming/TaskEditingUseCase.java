package use_case.port.incoming;

import use_case.dto.TaskCreationCommand;
import use_case.dto.TaskUpdateCommand;
import entity.Assessment;
import entity.TaskStatus;
import java.util.List;
import java.util.Optional;

/**
 * Allows adapters to create, view, mutate, or remove assessments (formerly tasks).
 * Note: Some methods are limited by AssessmentRepository capabilities.
 */
public interface TaskEditingUseCase {
    Assessment createTask(TaskCreationCommand command);
    List<Assessment> listTasksForUser(String courseId, TaskStatus statusFilter);
    Optional<Assessment> getTaskById(String assessmentId);
    void updateTask(TaskUpdateCommand command);
    void deleteTask(String assessmentId);
}
