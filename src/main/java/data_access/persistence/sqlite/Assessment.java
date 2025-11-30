package data_access.persistence.sqlite;

import entity.AssessmentType;
import use_case.repository.AssessmentRepository;
import app.Main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Assessment implements AssessmentRepository {

    private final Connection connection = Main.getConnection();;

    /**
     * Core functionalities
     * findByCourseId(String courseId): Retrieves all the Assessments related to a course.
     * @param courseId: the course
     * @return a list of Course.
     */
    @Override
    public List<entity.Assessment> findByCourseId(String courseId) {
        List<entity.Assessment> assessmentList = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            String getAssessment = "select * from assessments WHERE course_id = '" + courseId +
                    "'";
            ResultSet result = stmt.executeQuery(getAssessment);
            while (result.next()) {
                String assessmentId = result.getString("assessment_id");
                String courseIdFromDb = result.getString("course_id");
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
                        assessmentId, courseIdFromDb, title,
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

    @Override
    public java.util.Optional<entity.Assessment> findById(String assessmentId) {
        try {
            Statement stmt = connection.createStatement();
            String getAssessment = "SELECT * FROM assessments WHERE assessment_id = '" + assessmentId + "'";
            ResultSet result = stmt.executeQuery(getAssessment);
            
            if (result.next()) {
                String courseId = result.getString("course_id");
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
                        assessmentId, courseId, title, type, grade,
                        startsAt, endsAt, durationMinutes, weight, location, notes
                );
                return java.util.Optional.of(assessment);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return java.util.Optional.empty();
    }

    @Override
    public void update(entity.Assessment assessment) {
        try {
            Statement stmt = connection.createStatement();
            String updateAssessment = "UPDATE assessments SET " +
                    "course_id = '" + assessment.getCourseId() + "', " +
                    "title = '" + assessment.getTitle() + "', " +
                    "type = '" + assessment.getType() + "', " +
                    "grade = " + assessment.getGrade() + ", " +
                    "starts_at = '" + assessment.getStartsAt() + "', " +
                    "ends_at = '" + assessment.getEndsAt() + "', " +
                    "duration_minutes = " + assessment.getDurationMinutes() + ", " +
                    "weight = " + assessment.getWeight() + ", " +
                    "location = '" + assessment.getLocation() + "', " +
                    "notes = '" + assessment.getNotes() + "' " +
                    "WHERE assessment_id = '" + assessment.getAssessmentId() + "'";
            stmt.executeUpdate(updateAssessment);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void deleteById(String assessmentId) {
        try {
            Statement stmt = connection.createStatement();
            String deleteAssessment = "DELETE FROM assessments WHERE assessment_id = '" + assessmentId + "'";
            stmt.executeUpdate(deleteAssessment);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
