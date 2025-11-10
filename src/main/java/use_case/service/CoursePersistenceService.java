package use_case.service;

import use_case.dto.CourseSnapshot;
import use_case.port.incoming.CoursePersistenceUseCase;
import use_case.port.outgoing.TransactionalPersistencePort;
import use_case.repository.CourseRepository;
import use_case.repository.ScheduleEventRepository;
import use_case.repository.TaskRepository;
import java.util.Objects;

/**
 * Persists and reloads a user's course ecosystem.
 */
public class CoursePersistenceService implements CoursePersistenceUseCase {
    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;
    private final ScheduleEventRepository scheduleEventRepository;
    private final TransactionalPersistencePort transactionalPersistencePort;

    public CoursePersistenceService(CourseRepository courseRepository,
                                    TaskRepository taskRepository,
                                    ScheduleEventRepository scheduleEventRepository,
                                    TransactionalPersistencePort transactionalPersistencePort) {
        this.courseRepository = Objects.requireNonNull(courseRepository, "courseRepository");
        this.taskRepository = Objects.requireNonNull(taskRepository, "taskRepository");
        this.scheduleEventRepository = Objects.requireNonNull(scheduleEventRepository,
                "scheduleEventRepository");
        this.transactionalPersistencePort = Objects.requireNonNull(transactionalPersistencePort,
                "transactionalPersistencePort");
    }

    @Override
    public void persistCourseState(String userId) {
        // TODO: coordinate repository writes inside a transaction.
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CourseSnapshot loadCourseState(String userId) {
        // TODO: hydrate snapshot from course, task, and event repositories.
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
