package domain.repository;

import domain.model.User;
import java.util.Optional;

/**
 * Persistence boundary for users.
 */
public interface UserRepository {
    Optional<User> findById(String userId);

    void save(User user);
}
