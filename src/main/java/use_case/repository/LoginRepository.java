package use_case.repository;

import entity.User;

/**
 * Persistence boundary for users.
 */
public interface LoginRepository {

    public String getPasswordByEmail(String email);

    public User getUserByEmail(String email);
}
