package use_case.repository;

import entity.User;

import java.sql.SQLException;

/**
 * Persistence boundary for users.
 */
public interface LoginRepository {

    User getUserByEmail(String email) throws SQLException;
}
