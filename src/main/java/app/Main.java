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

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (currentConnection != null && !currentConnection.isClosed()) {
                    currentConnection.close();
                    System.out.println("Database connection closed");
                }
            } catch (SQLException e) {
            }
        }));

        SwingUtilities.invokeLater(() -> {
            AppBuilder appBuilder = new AppBuilder();
            JFrame application = appBuilder
                    .addWelcomeView()
                    .addWelcomeUseCase()
                    .addSignUpView()
                    .addSignUpUseCase()
                    .addDashboardView()
                    .addDashboardUseCase()
                    .addSyllabusUploadView()
                    .addSyllabusUploadUseCase()
                    .addLoginView()
                    .addLoginUseCase()
                    .addCalendarExportView()
                    .addCalendarExportUseCase()
                    .addTaskListView()
                    .addTaskListUseCase()
                    .addGradeCalculatorView()
                    .addGradeCalculatorUseCase()
                    .build();

            application.pack();
            application.setSize(900, 600);
            application.setLocationRelativeTo(null);
            application.setVisible(true);
        });
    }
}