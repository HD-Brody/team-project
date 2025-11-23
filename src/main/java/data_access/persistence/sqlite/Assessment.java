package data_access.persistence.sqlite;

import entity.AssessmentType;
import use_case.repository.AssessmentRepository;
import view.cli.Main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Assessment implements AssessmentRepository {

    private final Connection connection = Main.getConnection();;

    /**
     * Core functionalities
     * findByCourseID(String courseID): Retrieves all the Assessments related to a course.
     * @param courseID: the course
     * @return a list of Course.
     */
    @Override
    public List<entity.Assessment> findByCourseID(String courseID) {
        List<entity.Assessment> assessmentList = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            String getAssessment = "select * from assessments WHERE course_id = '" + courseID +
                    "'";
            ResultSet result = stmt.executeQuery(getAssessment);
            while (result.next()) {
                String assessmentId = result.getString("assessment_id");
                String courseId = result.getString("course_id"); // Use a different var name
                String title = result.getString("title");
                AssessmentType type = AssessmentType.valueOf(result.getString("type"));
                double grade = result.getDouble("grade");
                String startsAt = result.getString("starts_at");
                String endsAt = result.getString("ends_at");
                Long durationMinutes = result.getLong("duration_minutes");
                Double weight = result.getDouble("weight");
                String location = result.getString("location");
                String notes = result.getString("notes");

                entity.Assessment assessment = new entity.Assessment(
                        assessmentId, courseId, title,
                        type,
                        grade,
                        startsAt,
                        endsAt,
                        durationMinutes,
                        weight,
                        location,
                        notes
                );
                assessmentList.add(assessment);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return assessmentList;
    }

    /**
     * Core functionalities
     * save(Assessment assessment): Saves a assessment.
     * @param assessment: a Assessment instance
     * @return null.
     */
    @Override
    public void save(entity.Assessment assessment) {
        try {
            Statement stmt = connection.createStatement();
            String saveAssessment = "INSERT INTO assessments VALUES ('" +
                    assessment.getAssessmentId() + "', '" +
                    assessment.getCourseId() + "', '" +
                    assessment.getTitle() + "', '" +
                    assessment.getType() + "', " +
                    assessment.getGrade() + ", '" +
                    assessment.getStartsAt() + "', '" +
                    assessment.getEndsAt() + "', " +
                    assessment.getDurationMinutes() + ", " +
                    assessment.getWeight() + ", '" +
                    assessment.getLocation() + "', '" +
                    assessment.getNotes() + "')";
            int x = stmt.executeUpdate(saveAssessment);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
