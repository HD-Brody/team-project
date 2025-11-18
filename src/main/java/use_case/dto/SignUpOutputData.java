package use_case.dto;

public class SignUpOutputData {

    private final String email;
    private final String message;

    public SignUpOutputData(String username, String message) {
        this.email = username;
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }
}
