package view.cli;

import java.sql.*;



/**
 * Temporary launcher to avoid confusion while the real UI is under development.
 *
 * <p>This stub simply provides a runnable entry point so teammates can invoke
 * the project with {@code mvn exec:java}. Replace the placeholder logic with the
 * actual Swing bootstrap once the presentation layer is ready.</p>
 */
public final class Main {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/db/syllabus_assistant.db";
    private static Connection currentConnection;

    private Main() {
        // Utility class
    }

    public static Connection getConnection() {
        return currentConnection;
    }

    public static void main(String[] args) {
        /**
         * This creates a DB connection
         */
        try {
            currentConnection = DriverManager.getConnection(DB_URL);
        }catch (Exception e) {
            System.out.println("Database connection failed");
        }



        System.out.println("Syllabus Assistant launcher placeholder.");
        System.out.println("Implement the UI and wire it up via interface_adapter and use_case.");
    }
}
