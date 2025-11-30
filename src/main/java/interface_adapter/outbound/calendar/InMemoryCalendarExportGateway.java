package interface_adapter.outbound.calendar;

import entity.Assessment;
import entity.ScheduleEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import use_case.repository.AssessmentRepository;
import use_case.repository.ScheduleEventRepository;

/**
 * Simple in-memory adapter that satisfies calendar export repositories without persistence.
 */
public class InMemoryCalendarExportGateway implements AssessmentRepository, ScheduleEventRepository {
    private final List<Assessment> assessments = new ArrayList<>();
    private final List<ScheduleEvent> scheduleEvents = new ArrayList<>();

    public InMemoryCalendarExportGateway addAssessment(Assessment assessment) {
        assessments.add(Objects.requireNonNull(assessment, "assessment"));
        return this;
    }

    public InMemoryCalendarExportGateway addScheduleEvent(ScheduleEvent event) {
        scheduleEvents.add(Objects.requireNonNull(event, "event"));
        return this;
    }

    public void clear() {
        assessments.clear();
        scheduleEvents.clear();
    }

    @Override
    public List<Assessment> findByCourseId(String courseId) {
        return assessments.stream()
                .filter(a -> Objects.equals(a.getCourseId(), courseId))
                .collect(Collectors.toList());
    }

    @Override
    public void save(Assessment assessment) {
        assessments.add(Objects.requireNonNull(assessment, "assessment"));
    }

    @Override
    public Optional<Assessment> findById(String assessmentId) {
        return assessments.stream()
                .filter(a -> Objects.equals(a.getAssessmentId(), assessmentId))
                .findFirst();
    }

    @Override
    public void update(Assessment assessment) {
        assessments.removeIf(a -> Objects.equals(a.getAssessmentId(), assessment.getAssessmentId()));
        assessments.add(Objects.requireNonNull(assessment, "assessment"));
    }

    @Override
    public void deleteById(String assessmentId) {
        assessments.removeIf(a -> Objects.equals(a.getAssessmentId(), assessmentId));
    }

    @Override
    public List<ScheduleEvent> findByUserId(String userId) {
        return scheduleEvents.stream()
                .filter(e -> Objects.equals(e.getUserId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public void save(ScheduleEvent event) {
        scheduleEvents.add(Objects.requireNonNull(event, "event"));
    }
}
