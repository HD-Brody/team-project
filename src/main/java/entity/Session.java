package entity;

public final class Session {

    private final String userID;
    private final String name;
    private final String email;
    private final long createdTimestampMillis;

    public Session(String userID, String name, String email, long createdTimestampMillis) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.createdTimestampMillis = createdTimestampMillis;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public long getCreatedTimestampMillis() {
        return createdTimestampMillis;
    }
}
