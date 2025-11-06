package application.port.incoming;

import application.dto.AuthenticationResult;
import application.dto.UserCredentials;
import application.dto.UserRegistrationCommand;

/**
 * Manages user authentication flows.
 */
public interface AuthenticationUseCase {
    AuthenticationResult login(UserCredentials credentials);

    AuthenticationResult register(UserRegistrationCommand command);
}
