package use_case.repository;

import entity.User;

/**
 * Persistence boundary for users.
 */
public interface LoginRepository {

    User getUserByUserID(String userID);

    String getPasswordByUserID(String userID);
}
