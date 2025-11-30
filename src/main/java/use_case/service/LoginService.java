package use_case.service;

import data_access.persistence.sqlite.Login;
import entity.Session;
import entity.User;
import use_case.port.outgoing.LoginOutputPort;
import use_case.dto.LoginInputData;
import use_case.port.incoming.LoginUseCase;
import use_case.port.outgoing.PasswordHashingPort;
import use_case.repository.SessionRepository;
import use_case.repository.LoginRepository;
import use_case.dto.LoginOutputData;
import use_case.util.HashUtil;
import use_case.util.ValidationUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginService implements LoginUseCase {

    private final LoginRepository userRepository;
    private final LoginOutputPort loginOutputPort;
    private final SessionRepository sessionRepository;

    public LoginService(LoginRepository loginRepository,
                        SessionRepository sessionRepository,
                        LoginOutputPort loginOutputPort
                        ) {
        this.userRepository = loginRepository;
        this.loginOutputPort = loginOutputPort;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void execute(LoginInputData loginInputData) {
        final String email = loginInputData.getEmail();
        final String password = loginInputData.getPassword();

        // When email is not valid
        if (!ValidationUtil.validateEmail(email)) {
            loginOutputPort.prepareFailView(new LoginOutputData(email, false, "Email is not valid, please try again"));
            return;
        }

        // Login main use case
        String emailDB;
        String passwordHashDB;

        User userDB = null;
        try {
            userDB = userRepository.getUserByEmail(email);
        }
        catch (Exception e) {
            loginOutputPort.prepareFailView(new LoginOutputData(email, false, "User not found"));
            return;
        }

        // When user not found
        if (userDB == null) {
            loginOutputPort.prepareFailView(new LoginOutputData(email, false, "User not found"));
            return;
        }

        // Extract user info
        emailDB = userDB.getEmail();
        passwordHashDB = userDB.getPasswordHash();

        if(password == null || password.isEmpty()) {
            loginOutputPort.prepareFailView(new LoginOutputData(email, false, "Password can't be empty"));
            return;
        }

        // Check input password hash and hashed password from db
        if (email.equals(emailDB) && HashUtil.matches(password, passwordHashDB)) {
            // Create and set session
            sessionRepository.setSession(new Session(
                    userDB.getUserId(),
                    userDB.getName(),
                    userDB.getEmail(),
                    System.currentTimeMillis()));

            // Pass userId in success output data
            loginOutputPort.prepareSuccessView(new LoginOutputData(
                    userDB.getUserId(),
                    email,
                    true,
                    "Login successful"));
        }
        else {
            loginOutputPort.prepareFailView(new LoginOutputData(email, false, "Password doesn't match, please try again"));
        }
    }

    @Override
    public void switchView(String viewName) {
        loginOutputPort.switchView(viewName);
    }
}
