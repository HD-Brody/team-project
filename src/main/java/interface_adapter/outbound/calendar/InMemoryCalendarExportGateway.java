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
    public Optional<Assessment> findById(String assessmentId) {
        return assessments.stream()
                .filter(a -> a.getAssessmentId().equals(assessmentId))
                .findFirst();
    }

    @Override
    public List<Assessment> findByCourseId(String courseId) {
        return assessments.stream()
                .filter(a -> a.getCourseId().equals(courseId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleEvent> findByUserId(String userId) {
        return scheduleEvents.stream()
                .filter(e -> e.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void saveAll(List items) {
        for (Object item : items) {
            if (item instanceof Assessment) {
                assessments.add((Assessment) item);
            } else if (item instanceof ScheduleEvent) {
                scheduleEvents.add((ScheduleEvent) item);
            } else {
                throw new IllegalArgumentException("Unsupported item type: " + item);
            }
        }
    }
}
