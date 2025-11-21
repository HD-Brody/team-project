package use_case.port.outgoing;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import use_case.dto.ScheduleEventSnapshot;

/**
 * Persistence boundary for retrieving scheduled events scoped to a user.
 */
public interface ScheduleEventQueryPort {
    List<ScheduleEventSnapshot> findScheduleEvents(String userId,
                                                   List<String> courseIds,
                                                   Optional<Instant> windowStart,
                                                   Optional<Instant> windowEnd);
}
