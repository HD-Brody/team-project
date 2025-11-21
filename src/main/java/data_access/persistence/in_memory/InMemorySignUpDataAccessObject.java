package data_access.persistence.in_memory;

import entity.User;
import use_case.repository.SignUpRepository;

import java.util.HashMap;

/**
 * Mock db for test purposes
 * */

public class InMemorySignUpDataAccessObject implements SignUpRepository {

    private final HashMap<String, String> usernameToUserIDDB;
    private final HashMap<String, String> db;

    public InMemorySignUpDataAccessObject() {
        this.usernameToUserIDDB = new HashMap<String, String>();
        this.db = new HashMap<String, String>();
    }

    @Override
    public void saveUser(String userID, String name, String email, String timezone, String password) {
        db.put(email, password);
        usernameToUserIDDB.put(email, userID);
    }

    public User getUserByEmail(String email) {
        if(db.containsKey(email)) {
            return new User(usernameToUserIDDB.get(email), "123", email, "123", db.get(email));
        }
        else {
            return null;
        }
    }
}
