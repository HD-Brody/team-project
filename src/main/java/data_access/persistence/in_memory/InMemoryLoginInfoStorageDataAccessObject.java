package data_access.persistence.in_memory;

/*
* Mock storage for test purposes.
* */

import entity.User;
import use_case.repository.LoginRepository;

import java.util.HashMap;
import java.util.UUID;

public class InMemoryLoginInfoStorageDataAccessObject implements LoginRepository {

    // mock db for storage
    private final HashMap<String, String> db;

    public InMemoryLoginInfoStorageDataAccessObject() {
        this.db = new HashMap<String, String>();
    }

    public void setUser(User user, String password) {
        db.put(user.getUserId(), password);
    }

    public void setUserByUsername(String username, String password) {
        db.put(username, password);
    }

    @Override
    public User getUserByUsername(String username) {
        String passwordDB = db.get(username);
        if(passwordDB == null) {
            return null;
        }
        else {
            return new User("", username, "123", "123");
        }
    }

    @Override
    public String getPasswordByUsername(String username) {
        return db.get(username);
    }

    @Override
    public String getPasswordByUserID(String userID) {
        return "";
    }
}
