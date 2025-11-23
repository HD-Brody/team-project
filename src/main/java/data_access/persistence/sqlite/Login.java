package data_access.persistence.sqlite;

import java.sql.*;
import entity.User;
import view.cli.Main;
import use_case.repository.LoginRepository;

public class Login implements LoginRepository {

    private final Connection connection = Main.getConnection();;

    /**
     * Core functionalities
     * getUserByEmail(String email): Retrieves a user from the DB using the provided email address.
     * @param email: user's email
     * @return a User entity of the user with the email.
     */

    public User getUserByEmail(String email) {
        try {
            Statement stmt = connection.createStatement();
            String getUser = "select * from users WHERE email = '" + email +
                    "'";
            ResultSet result = stmt.executeQuery(getUser);

            String userId = result.getString("user_id");
            String name = result.getString("name");
            String timezone = result.getString("timezone");
            String passwordHash = result.getString("password_hash");

            return new User(userId, name, email, timezone, passwordHash);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}