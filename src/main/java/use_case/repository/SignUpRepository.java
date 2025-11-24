package use_case.repository;

import java.sql.SQLException;

public interface SignUpRepository {

    void saveUser(String userID, String name, String email, String timezone, String password) throws SQLException;

}
