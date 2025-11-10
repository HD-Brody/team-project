package use_case.port.incoming;

import use_case.dto.AuthenticationResult;
import use_case.dto.UserCredentials;
import use_case.dto.UserRegistrationCommand;

/**
 * Manages user authentication flows.
 */
public interface AuthenticationUseCase {
    AuthenticationResult login(UserCredentials credentials);

    AuthenticationResult register(UserRegistrationCommand command);
}
