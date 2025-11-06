package domain.repository;

import domain.model.ScheduleEvent;
import java.util.List;

/**
 * Persistence boundary for calendar events.
 */
public interface ScheduleEventRepository {
    List<ScheduleEvent> findByUserId(String userId);

    void saveAll(List<ScheduleEvent> events);
}
