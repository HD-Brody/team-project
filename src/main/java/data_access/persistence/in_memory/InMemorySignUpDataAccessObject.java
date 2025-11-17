package data_access.persistence.in_memory;

import entity.User;
import use_case.repository.SignUpRepository;

import java.util.HashMap;
import java.util.UUID;

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
        UUID uuid = UUID.randomUUID();
        db.put(name, password);
        usernameToUserIDDB.put(name, uuid.toString());
    }

    public User getUserByUsername(String username) {
        if(db.containsKey(username))
            return new User(usernameToUserIDDB.get(username), username, "123", "123");
        else
            return null;
    }
}
