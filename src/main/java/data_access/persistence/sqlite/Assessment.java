package data_access.persistence.sqlite;

import use_case.repository.AssessmentRepository;

import java.sql.Statement;

public class Assessment implements AssessmentRepository {

    @Override
    public void save(Assessment assessments) {}
    @Override
    public void saveAll(entity.Assessment assessments) {
        try {
            Statement stmt = connection.createStatement();
            String saveAssessment = "INSERT INTO assessments VALUES ('" +
                    assessments.getAssessmentId() + "', '" +
                    assessments.getCourseId() + "', '" +
                    assessments.getTitle() + "', '" +
                    assessments.getType() + "', " +
                    assessments.getGrade() + ", '" +
                    assessments.getStartsAt() + "', '" +
                    assessments.getEndsAt() + "', " +
                    assessments.getDurationMinutes() + ", " +
                    assessments.getWeight() + ", '" +
                    assessments.getLocation() + "', '" +
                    assessments.getNotes() + "')";
            int x = stmt.executeUpdate(saveAssessment);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
