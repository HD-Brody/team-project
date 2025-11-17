package interface_adapter.outbound.calendar;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import use_case.dto.ScheduleEventSnapshot;
import use_case.dto.ScheduledTaskSnapshot;
import use_case.port.outgoing.ScheduleEventQueryPort;
import use_case.port.outgoing.ScheduledTaskQueryPort;

/**
 * Simple in-memory adapter that satisfies the calendar export query ports without requiring a
 * persistence layer. Useful for demos and early integration testing.
 */
public class InMemoryCalendarExportGateway implements ScheduledTaskQueryPort, ScheduleEventQueryPort {
    private final List<ScheduledTaskSnapshot> taskSnapshots = new ArrayList<>();
    private final List<ScheduleEventSnapshot> scheduleEventSnapshots = new ArrayList<>();

    public InMemoryCalendarExportGateway addTask(ScheduledTaskSnapshot snapshot) {
        taskSnapshots.add(Objects.requireNonNull(snapshot, "snapshot"));
        return this;
    }

    public InMemoryCalendarExportGateway addScheduleEvent(ScheduleEventSnapshot snapshot) {
        scheduleEventSnapshots.add(Objects.requireNonNull(snapshot, "snapshot"));
        return this;
    }

    public void clear() {
        taskSnapshots.clear();
        scheduleEventSnapshots.clear();
    }

    @Override
    public List<ScheduledTaskSnapshot> findTasksForExport(String userId,
                                                          List<String> courseIds,
                                                          Optional<Instant> windowStart,
                                                          Optional<Instant> windowEnd) {
        return taskSnapshots.stream()
                .filter(snapshot -> snapshot.getUserId().equals(userId))
                .filter(snapshot -> courseIds.isEmpty()
                        || (snapshot.getCourseId() != null && courseIds.contains(snapshot.getCourseId())))
                .filter(snapshot -> withinWindow(snapshot.getDueAt(), windowStart, windowEnd))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    @Override
    public List<ScheduleEventSnapshot> findScheduleEvents(String userId,
                                                          List<String> courseIds,
                                                          Optional<Instant> windowStart,
                                                          Optional<Instant> windowEnd) {
        return scheduleEventSnapshots.stream()
                .filter(snapshot -> snapshot.getUserId().equals(userId))
                .filter(snapshot -> withinWindow(snapshot.getStartsAt(), windowStart, windowEnd))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    private boolean withinWindow(Instant instant, Optional<Instant> windowStart,
                                 Optional<Instant> windowEnd) {
        if (instant == null) {
            return false;
        }
        if (windowStart.isPresent() && instant.isBefore(windowStart.get())) {
            return false;
        }
        return windowEnd.isEmpty() || !instant.isAfter(windowEnd.get());
    }
}
