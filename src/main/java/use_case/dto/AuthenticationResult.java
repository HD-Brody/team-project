package use_case.dto;

import java.util.Objects;

/**
 * Outcome of authentication attempts.
 */
public final class AuthenticationResult {
    private final boolean success;
    private final String userId;
    private final String message;
    private final String authToken;

    public AuthenticationResult(boolean success, String userId, String message, String authToken) {
        this.success = success;
        this.userId = userId;
        this.message = Objects.requireNonNull(message, "message");
        this.authToken = authToken;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthToken() {
        return authToken;
    }
}
