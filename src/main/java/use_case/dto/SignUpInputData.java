package use_case.dto;


public class SignUpInputData {

    private final String username;
    private final String password;

    public SignUpInputData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
