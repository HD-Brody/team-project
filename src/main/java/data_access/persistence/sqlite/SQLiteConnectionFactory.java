package data_access.persistence.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Calls to DB require a connection instance from java.sql
 * Create a session HERE before accessing data using the data interface.
 */
public class SQLiteConnectionFactory {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/db/syllabus_assistant.db";

    /**
     * CALL THIS METHOD!
     * Example usage: Connection currentConnection = SQLiteConnectionFactory().get_connection();
     * May add try & catch for safety
     * @return a Connection instance
     * @throws SQLException
     */
    public static Connection get_connection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
