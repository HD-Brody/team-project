package use_case.port.outgoing;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import use_case.dto.ScheduledTaskSnapshot;

/**
 * Persistence boundary for retrieving tasks that can be exported as calendar events.
 */
public interface ScheduledTaskQueryPort {
    List<ScheduledTaskSnapshot> findTasksForExport(String userId,
                                                   List<String> courseIds,
                                                   Optional<Instant> windowStart,
                                                   Optional<Instant> windowEnd);
}
