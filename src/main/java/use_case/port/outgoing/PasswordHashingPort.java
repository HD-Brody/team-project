package use_case.port.outgoing;

/**
 * Hashes and verifies user passwords.
 */
public interface PasswordHashingPort {
    String hash(String rawPassword);

    boolean matches(String rawPassword, String hashedPassword);
}
