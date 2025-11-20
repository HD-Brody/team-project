package data_access.persistence.sqlite;
import entity.Assessment;
import view.cli.Main;
import java.sql.*;
import use_case.repository.SyllabusRepository;
import use_case.repository.AssessmentRepository;


public class Syllabus implements SyllabusRepository, AssessmentRepository  {

    /**
     * Initialize with the Connection instance.
     * Ex. UploadSyllabus newInstance = new UploadSyllabus(connection);
     */
    private final Connection connection = Main.getConnection();

    /**
     * Save the syllabus to the syllabi table inside the DB.
     *
     * Syllabus is defined as:
     * private final String syllabusId;
     * private final String courseId;
     * private final String sourceFilePath;
     */
    @Override
    public void save(entity.Syllabus syllabus) {
        try {
            Statement stmt = connection.createStatement();
            String saveSyllabus = "insert into syllabi values ('" + syllabus.getSyllabusId()+ "', '" + syllabus.getCourseId()
                    + "', '" + syllabus.getSourceFilePath() + "')";
            int x = stmt.executeUpdate(saveSyllabus);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void saveAll(Assessment assessments) {
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
