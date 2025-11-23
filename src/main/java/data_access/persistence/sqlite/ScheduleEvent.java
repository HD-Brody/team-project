package data_access.persistence.sqlite;

import entity.SourceKind;
import entity.SourceKind;
import use_case.repository.ScheduleEventRepository;
import view.cli.Main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ScheduleEvent implements ScheduleEventRepository {

    private final Connection connection = Main.getConnection();

    /**
     * Core functionalities
     * findByCourseID(String userID): Retrieves all the ScheduleEvent a user has.
     *
     * @param userId: the user
     * @return a list of ScheduleEvent.
     */
    @Override
    public List<entity.ScheduleEvent> findByUserID(String userId) {
        List<entity.ScheduleEvent> eventList = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            String getEvent = "select * from schedule_events WHERE user_id = '" + userID +
                    "'";
            ResultSet result = stmt.executeQuery(getEvent);
            while (result.next()) {
                String eventId = result.getString("event_id"); // Use a different var name
                String userId = result.getString("user_id");
                String title = result.getString("title");
                String startsAt = result.getString("starts_at");
                String endsAt = result.getString("ends_at");
                String location = result.getString("location");
                String notes = result.getString("notes");
                SourceKind sourceKind = SourceKind.valueOf(result.getString("source_kind"));
                String sourceId = result.getString("source_id");

                entity.ScheduleEvent event = new entity.ScheduleEvent(
                        eventId,
                        userId,
                        title,
                        startsAt,
                        endsAt,
                        location,
                        notes,
                        sourceKind,
                        sourceId
                );
                eventList.add(event);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return eventList;
    }

    @Override
    public void save(entity.ScheduleEvent event) {
        try {
            Statement stmt = connection.createStatement();
            String saveEvent = "INSERT INTO courses VALUES ('" +
                    event.getEventId() + "', '" +
                    event.getUserId() + "', '" +
                    event.getTitle() + "', " +
                    event.getStartsAt() + ", " +
                    event.getEndsAt() + ", " +
                    event.getLocation() + ", " +
                    event.getNotes() + ", " +
                    event.getSource() + ", " +
                    event.getSourceId() + "')";
            int x = stmt.executeUpdate(saveEvent);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}