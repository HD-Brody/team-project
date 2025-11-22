package use_case.dto;

public class SignUpOutputData {

    private final String email;
    private final String message;

    public SignUpOutputData(String email, String message) {
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
