package use_case.dto;

public class SignUpOutputData {

    private final String email;
    private final String message;
    private final boolean isSuccess;

    public SignUpOutputData(String email, boolean isSuccess, String message) {
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
