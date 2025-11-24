package use_case.dto;

public class LoginOutputData {

    private final String email;
    private final String message;
    private final boolean isSuccess;

    public LoginOutputData(String email) {
        this(email, true, "");
    }

    public LoginOutputData(String email, boolean isSuccess, String message) {
        this.email = email;
        this.message = message;
        this.isSuccess = isSuccess;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }
}
