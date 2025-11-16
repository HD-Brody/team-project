package use_case.repository;

import entity.User;

public interface SignUpRepository {

    void saveUser(User user, String password);

}
