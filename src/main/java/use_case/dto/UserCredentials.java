package use_case.dto;

import java.util.Objects;

/**
 * Credentials used during login flows.
 */
public final class UserCredentials {
    private final String username;
    private final String password;

    public UserCredentials(String username, String password) {
        this.username = Objects.requireNonNull(username, "username");
        this.password = Objects.requireNonNull(password, "password");
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
