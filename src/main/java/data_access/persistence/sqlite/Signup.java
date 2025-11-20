package data_access.persistence.sqlite;

import java.sql.*;
import entity.User;

public class Signup {

    private final Connection connection;

    /**
     * Core functionalities
     * saveUser
     */
    void saveUser(String userID, String name, String email, String timezone, String password) {
        try {
            Statement stmt = connection.createStatement();
            String storeUser = "insert into users values ('" +userID+ "', '" +name+ "', '" +email+ "', '" +timezone+ "', '"  + password + "')";
            int x = stmt.executeUpdate(storeUser);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}