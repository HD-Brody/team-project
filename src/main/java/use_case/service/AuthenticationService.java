package use_case.service;

import use_case.dto.AuthenticationResult;
import use_case.dto.UserCredentials;
import use_case.dto.UserRegistrationCommand;
import use_case.port.incoming.AuthenticationUseCase;
import use_case.port.outgoing.PasswordHashingPort;
import use_case.port.outgoing.TokenProviderPort;
import entity.User;
import use_case.repository.UserRepository;
import java.util.Objects;

/**
 * Implements login and registration flows.
 */
public class AuthenticationService implements AuthenticationUseCase {
    private final UserRepository userRepository;
    private final PasswordHashingPort passwordHashingPort;
    private final TokenProviderPort tokenProviderPort;

    public AuthenticationService(UserRepository userRepository,
                                 PasswordHashingPort passwordHashingPort,
                                 TokenProviderPort tokenProviderPort) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
        this.passwordHashingPort = Objects.requireNonNull(passwordHashingPort, "passwordHashingPort");
        this.tokenProviderPort = Objects.requireNonNull(tokenProviderPort, "tokenProviderPort");
    }

    @Override
    public AuthenticationResult login(UserCredentials credentials) {
        // TODO: validate credentials and issue tokens.
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public AuthenticationResult register(UserRegistrationCommand command) {
        // TODO: register new users and persist hashed credentials.
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private void ensureUnique(User user) {
        // Placeholder hook for additional domain checks during registration.
    }
}
