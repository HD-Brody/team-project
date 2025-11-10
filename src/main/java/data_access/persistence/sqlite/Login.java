package data_access.persistence.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

    private final Connection connection;

    /**
     *
     */
    public Login(Connection connection) {
        this.connection = connection;
    }

    // --- Core Login/Authentication Functions ---

    /**
     * Finds a user by their email address.
     * @param email The user's email.
     * @return A User object (or a simple data structure/Map) containing user_id and password_hash, or null if not found.
     * @throws SQLException if a database access error occurs.
     */
    public UserData findUserByEmail(String email) throws SQLException {
        // SQL to retrieve the necessary data for login verification
        String sql = "SELECT user_id, password_hash FROM users WHERE email = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Assuming UserData is a simple class to hold the retrieved fields
                    // In a real application, you'd define this UserData class
                    return new UserData(
                            resultSet.getString("user_id"),
                            resultSet.getString("password_hash")
                    );
                }
                return null; // User not found
            }
        }
    }

    /**
     * Registers a new user in the database.
     * @param userId A unique ID for the new user (e.g., a generated UUID).
     * @param name The user's name.
     * @param email The user's email (must be unique).
     * @param passwordHash The securely hashed password.
     * @param timezone The user's timezone.
     * @return true if registration was successful, false otherwise.
     * @throws SQLException if a database access error occurs (e.g., email duplicate).
     */
    public boolean registerNewUser(
            String userId, String name, String email, String passwordHash, String timezone)
            throws SQLException
    {
        // The created_at and updated_at fields use the database default (datetime('now'))
        String sql = "INSERT INTO users (user_id, name, email, timezone, password_hash) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            statement.setString(2, name);
            statement.setString(3, email);
            statement.setString(4, timezone);
            statement.setString(5, passwordHash);

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        }
    }

    // --- Helper Class (Mockup) ---

    /**
     * A simple inner class or record to represent the minimal data needed for authentication.
     */
    public static class UserData {
        public final String userId;
        public final String passwordHash;

        public UserData(String userId, String passwordHash) {
            this.userId = userId;
            this.passwordHash = passwordHash;
        }
    }
}