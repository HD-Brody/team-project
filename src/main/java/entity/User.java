package entity;

import java.util.Objects;

/**
 * Represents a user of the syllabus assistant.
 */
public final class User {
    private final String userId;
    private final String name;
    private final String email;
    private final String timezone;

    public User(String userId, String name, String email, String timezone) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.name = Objects.requireNonNull(name, "name");
        this.email = Objects.requireNonNull(email, "email");
        this.timezone = Objects.requireNonNull(timezone, "timezone");
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getTimezone() {
        return timezone;
    }
}
