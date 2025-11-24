package use_case.repository;

import entity.User;

/**
 * Persistence boundary for users.
 */
public interface LoginRepository {

    User getUserByEmail(String email);
}
