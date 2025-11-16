package use_case.repository;

import entity.User;

/**
 * Persistence boundary for users.
 */
public interface LoginRepository {

    User getUserByUsername(String username);

    String getPasswordByUserID(String userID);
}
