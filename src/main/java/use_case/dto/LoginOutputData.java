package use_case.dto;

public class LoginOutputData {

    private final String email;
    private final String message;

    public LoginOutputData(String email) {
        this(email, "");
    }

    public LoginOutputData(String email, String message) {
        this.email = email;
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }
}
