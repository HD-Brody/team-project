package use_case.dto;

public class LoginOutputData {

    private final String userId;
    private final String email;
    private final String message;
    private final boolean isSuccess;

    public LoginOutputData(String email) {
        this(null, email, true, "");
    }

    public LoginOutputData(String email, boolean isSuccess, String message) {
        this(null, email, isSuccess, message);
    }

    public LoginOutputData(String userId, String email, boolean isSuccess, String message) {
        this.userId = userId;
        this.email = email;
        this.message = message;
        this.isSuccess = isSuccess;
    }

    public String getUserId() {
        return userId;
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
