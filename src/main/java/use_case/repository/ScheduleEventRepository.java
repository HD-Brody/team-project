package use_case.repository;

import entity.ScheduleEvent;
import java.util.List;

/**
 * Persistence boundary for calendar events.
 */
public interface ScheduleEventRepository {
    List<ScheduleEvent> findByUserId(String userId);

    void saveAll(List<ScheduleEvent> events);
}
