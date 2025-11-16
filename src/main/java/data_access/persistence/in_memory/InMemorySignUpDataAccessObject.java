package data_access.persistence.in_memory;

import entity.User;
import use_case.repository.SignUpRepository;

import java.util.HashMap;

/**
 * Mock db for test purposes
 * */

public class InMemorySignUpDataAccessObject implements SignUpRepository {

    private final HashMap<String, String> db;

    public InMemorySignUpDataAccessObject() {
        this.db = new HashMap<String, String>();
    }

    @Override
    public void saveUser(String userID, String name, String email, String timezone, String password) {
        db.put(userID, password);
    }

    public User getUserByUsername(String username) {
        if(db.containsKey(username))
            return new User(username, username, "123", "123");
        else
            return null;
    }
}
