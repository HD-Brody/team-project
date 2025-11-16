package use_case.repository;

import entity.User;

public interface SignUpRepository {

    void saveUser(String userID, String name, String email, String timezone, String password);

}
