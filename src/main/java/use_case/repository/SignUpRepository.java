package use_case.repository;

public interface SignUpRepository {

    void saveUser(String userID, String name, String email, String timezone, String password);

}
