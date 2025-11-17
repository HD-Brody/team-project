package use_case.repository;

import entity.User;

/**
 * Persistence boundary for users.
 */
public interface LoginRepository {

    User getUserByUsername(String userID);

    String getPasswordByUserID(String userID);

    String getPasswordByUsername(String username);
}
