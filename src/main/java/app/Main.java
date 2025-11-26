package app;

import javax.swing.*;
import java.sql.*;



public class Main {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/db/syllabus_assistant.db";
    private static Connection currentConnection;

    public static Connection getConnection() {
        return currentConnection;
    }

    public static void main(String[] args) {
        /**
         * This creates a DB connection
         */
        try {
            currentConnection = DriverManager.getConnection(DB_URL);
            System.out.println("Database connection established successfully");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
            return; // Exit if database connection fails
        }

        SwingUtilities.invokeLater(() -> {
            AppBuilder appBuilder = new AppBuilder();
            JFrame application = appBuilder
                    .addSignUpView()
                    .addSignUpUseCase()
                    .addLoginView()
                    .addLoginUseCase()
                    .addSyllabusUploadView()
                    .addSyllabusUploadUseCase()
                    .addDashboardView()
                    .addDashboardUseCase()
                    .build();

            application.pack();
            application.setSize(700, 500);
            application.setLocationRelativeTo(null);
            application.setVisible(true);
        });
    }
}