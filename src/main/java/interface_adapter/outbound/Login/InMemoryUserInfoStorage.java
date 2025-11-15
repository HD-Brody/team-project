package interface_adapter.outbound.Login;

/*
* Mock storage for test purposes.
* */

import entity.User;
import use_case.repository.UserRepository;

import java.util.HashMap;

public class InMemoryUserInfoStorage implements UserRepository {

    // mock db for storage
    private final HashMap<String, String> db;

    public InMemoryUserInfoStorage() {
        this.db = new HashMap<String, String>();
    }

    public void setUser(User user, String password) {
        db.put(user.getUserId(), password);
    }

    public void setUserByUserID(String userID, String password) {
        db.put(userID, password);
    }

    @Override
    public User getUserByUsername(String username) {
        String passwordDB = db.get(username);
        if(passwordDB == null) {
            return null;
        }
        else {
            return new User(username, passwordDB, "123", "123");
        }
    }

    @Override
    public String getPasswordByUserID(String userID) {
        return db.get(userID);
    }
}
