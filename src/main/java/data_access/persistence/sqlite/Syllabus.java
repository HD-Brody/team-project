package data_access.persistence.sqlite;
import entity.Assessment;
import entity.User;
import app.Main;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import use_case.repository.SyllabusRepository;


public class Syllabus implements SyllabusRepository  {

    private final Connection connection = Main.getConnection();

    /**
     * Core functionalities
     * save(Syllabus syllabus): Saves a syllabus.
     * @param syllabus: a Syllabus instance
     * @return null.
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

    /**
     * Core functionalities
     * List<entity.Syllabus> findSyllabusByCourseID(String courseID):
     *     Retrieves all the syllabus related to a course.
     * @param courseID: the course
     * @return a list of Syllabus.
     */
    @Override
    public List<entity.Syllabus> findSyllabusByCourseID(String courseID) {
        List<entity.Syllabus> syllabusList = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            String getSyllabus = "select * from syllabi WHERE course_id = '" + courseID +
                    "'";
            ResultSet result = stmt.executeQuery(getSyllabus);
            while (result.next()) {
                String syllabus_id = result.getString("syllabus_id");
                String retrieved_course_id = result.getString("course_id"); // Use a different var name
                String source_file_path = result.getString("source_file_path");

                entity.Syllabus syllabus = new entity.Syllabus(syllabus_id, retrieved_course_id, source_file_path);
                syllabusList.add(syllabus);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return syllabusList;
    }
}
