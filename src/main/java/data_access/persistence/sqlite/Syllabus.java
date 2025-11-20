package data_access.persistence.sqlite;
import view.cli.Main;
import java.sql.*;
import use_case.repository.SyllabusRepository;


public class Syllabus implements SyllabusRepository,  {

    /**
     * Initialize with the Connection instance.
     * Ex. UploadSyllabus newInstance = new UploadSyllabus(connection);
     */
    private final Connection connection = Main.getConnection();

    /**
     * Syllabus is defined as:
     * private final String syllabusId;
     * private final String courseId;
     * private final String sourceFilePath;
     */
    @Override
    public void save(entity.Syllabus syllabus) {

        try {
            Statement stmt = connection.createStatement();
            String saveSyllabus = "insert into syllabi values ('" +userID+ "', '" +name+ "', '" +email+ "', '" +timezone+ "', '"  + password + "')";
            int x = stmt.executeUpdate(storeUser);
        } catch (Exception e) {
            System.out.println(e);
        }
    }



}
