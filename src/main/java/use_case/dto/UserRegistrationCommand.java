package use_case.dto;

import java.util.Objects;

/**
 * Registration data for new users.
 */
public final class UserRegistrationCommand {
    private final String email;
    private final String username;
    private final String password;
    private final String timezone;

    public UserRegistrationCommand(String email, String username, String password, String timezone) {
        this.email = Objects.requireNonNull(email, "email");
        this.username = Objects.requireNonNull(username, "username");
        this.password = Objects.requireNonNull(password, "password");
        this.timezone = Objects.requireNonNull(timezone, "timezone");
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getTimezone() {
        return timezone;
    }
}
