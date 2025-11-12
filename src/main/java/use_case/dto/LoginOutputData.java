package use_case.dto;

public class LoginOutputData {

    private final String username;

    public LoginOutputData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
