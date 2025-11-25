package data_access.persistence.in_memory;

/*
* Mock storage for test purposes.
* */

import entity.User;
import use_case.repository.LoginRepository;

import java.util.HashMap;

public class InMemoryLoginInfoStorageDataAccessObject implements LoginRepository {

    // mock db for storage
    private final HashMap<String, String> db;

    public InMemoryLoginInfoStorageDataAccessObject() {
        this.db = new HashMap<String, String>();

        db.put("admin@proj.com", "d033e22ae348aeb5660fc2140aec35850c4da997");
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
            return new User("", "notAUser", email, "", passwordDB);
        }
    }
}
