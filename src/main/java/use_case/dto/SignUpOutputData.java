package use_case.dto;

public class SignUpOutputData {

    private final String username;
    private final String message;

    public SignUpOutputData(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }
}
