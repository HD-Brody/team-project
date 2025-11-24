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

    public void setUserByEmail(String email, String password) {
        db.put(email, password);
    }

    @Override
    public User getUserByEmail(String email) {
        String passwordDB = db.get(email);
        if(passwordDB == null) {
            return null;
        }
        else {
            return new User("", email, "123", "123", "111");
        }
    }

    @Override
    public String getPasswordByEmail(String email) {
        return db.get(email);
    }
}
