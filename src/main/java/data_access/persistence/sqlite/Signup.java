package data_access.persistence.sqlite;

import java.sql.*;
import entity.User;
import app.Main;
import use_case.repository.SignUpRepository;

public class Signup implements SignUpRepository {

    private final Connection connection = Main.getConnection();;

    /**
     * Core functionalities
     * saveUser(String userID, String name, String email, String timezone, String password):
     *     Saves user
     * @param (userID, name, email, timezone, password)
     * @return null
     */
    public void saveUser(String userID, String name, String email, String timezone, String password) {
        try {
            Statement stmt = connection.createStatement();
            String storeUser = "insert into users values ('" +userID+ "', '" +name+ "', '" +email+ "', '" +timezone+ "', '"  + password + "')";
            int x = stmt.executeUpdate(storeUser);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}