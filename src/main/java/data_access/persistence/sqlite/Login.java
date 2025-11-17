package data_access.persistence.sqlite;

import java.sql.*;
import entity.User;

public class Login {

    private final Connection connection;

    /**
     * Core functionalities
     * getUserByUsername and getPasswordByUserID
     */
    public User getUserByUsername(String username) {
        try {
            Statement stmt = connection.createStatement();
            String getUser = "select user_id, name, email, timezone from users WHERE name = '" + username +
                    "'";
            ResultSet result = stmt.executeQuery(getUser);

            String userId = result.getString("user_id");
            String name = result.getString("name");
            String email = result.getString("email");
            String timezone = result.getString("timezone");

            return new User(userId, name, email, timezone);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getPasswordByUserID(String userID) {
        try {
            Statement stmt = connection.createStatement();
            String getUser = "select password_hash from users WHERE user_id = '" + userID +
                    "'";
            ResultSet result = stmt.executeQuery(getUser);

            return result.getString("password_hash");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}