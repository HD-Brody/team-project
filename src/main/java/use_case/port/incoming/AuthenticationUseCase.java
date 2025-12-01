package use_case.port.incoming;

import use_case.dto.AuthenticationResultOutputData;
import use_case.dto.UserCredentials;
import use_case.dto.UserRegistrationCommand;

/**
 * Manages user authentication flows.
 */
public interface AuthenticationUseCase {
    AuthenticationResultOutputData login(UserCredentials credentials);

    AuthenticationResultOutputData register(UserRegistrationCommand command);
}
