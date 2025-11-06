package application.port.outgoing;

/**
 * Issues authentication tokens for stateless sessions.
 */
public interface TokenProviderPort {
    String issueToken(String userId);
}
