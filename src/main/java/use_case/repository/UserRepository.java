package use_case.repository;

import entity.User;
import java.util.Optional;

/**
 * Persistence boundary for users.
 */
public interface UserRepository {

    User getUserByUsername(String username);

    String getPasswordByUserID(String userID);
}
