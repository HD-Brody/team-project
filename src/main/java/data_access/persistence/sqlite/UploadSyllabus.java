package data_access.persistence.sqlite;

import java.sql.*;

public class UploadSyllabus implements  {

    /**
     * Initialize with the Connection instance.
     * Ex. UploadSyllabus newInstance = new UploadSyllabus(connection);
     */
    private final Connection connection;
    connection = something in main.java
    // we are going to define a Connnection instance at startup.
//    public UploadSyllabus(Connection connection) {
//        this.connection = connection;
//    }

    public String returnSourceFilePath (String courseID) {
        try {
            Statement stmt = connection.createStatement();
            String getFile = "select source_file_path from syllabi WHERE user_id = '" + userID +
                    "' AND course_id = '" + courseID + "'";
            ResultSet result = stmt.executeQuery(getFile);
            return result.getString("source_file_path");
        } catch (Exception e) {
            System.out.println(e);
        }
    }


}
