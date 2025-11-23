package use_case.repository;

import entity.ScheduleEvent;
import java.util.List;

/**
 * Persistence boundary for calendar events.
 */
public interface ScheduleEventRepository {
    public List<ScheduleEvent> findByUserId(String userId);
    public void save(ScheduleEvent events);
}
