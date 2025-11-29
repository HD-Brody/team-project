package data_access.persistence.in_memory;

import entity.User;
import use_case.repository.SignUpRepository;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Mock db for test purposes
 * */

public class InMemorySignUpDataAccessObject implements SignUpRepository {

    private final HashMap<String, String> emailToUserIDDB;
    private final HashMap<String, String> db;
    private final HashMap<String, String> nameDB;

    public InMemorySignUpDataAccessObject() {
        this.emailToUserIDDB = new HashMap<String, String>();
        this.db = new HashMap<String, String>();
        this.nameDB = new HashMap<String, String>();
    }

    @Override
    public void saveUser(String userID, String name, String email, String timezone, String password) throws SQLException{
        if (nameDB.containsValue(name))
            throw new SQLException();
        if (db.containsKey(email))
            throw new SQLException();

        db.put(email, password);
        emailToUserIDDB.put(email, userID);
        nameDB.put(email, name);
    }

    public User getUserByEmail(String email) {
        if(db.containsKey(email)) {
            return new User(emailToUserIDDB.get(email), nameDB.get(email), email, "123", db.get(email));
        }
        else {
            return null;
        }
    }

    public void cleanDB() {
        this.db.clear();
        this.nameDB.clear();
        this.emailToUserIDDB.clear();
    }
}
