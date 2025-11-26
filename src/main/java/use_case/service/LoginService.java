package use_case.service;

import data_access.persistence.sqlite.Login;
import entity.Session;
import entity.User;
import use_case.port.outgoing.LoginOutputPort;
import use_case.dto.LoginInputData;
import use_case.port.incoming.LoginUseCase;
import use_case.repository.SessionRepository;
import use_case.repository.LoginRepository;
import use_case.dto.LoginOutputData;

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
        if (!validateEmail(email)) {
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
            loginOutputPort.prepareFailView(new LoginOutputData(email, false, ""));
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

        String passwordHash;

        // Hash input password
        try {
            passwordHash = passwordHashing(password);
        }
        catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                loginOutputPort.prepareFailView(new LoginOutputData(email, false, "Password can't be empty"));
            }
            else {
                loginOutputPort.prepareFailView(new LoginOutputData(email, false, "Unexpected error, please try again"));
            }
            return;
        }

        // Check input password hash and hashed password from db
        if (email.equals(emailDB) && passwordHash.equals(passwordHashDB)) {
            loginOutputPort.prepareSuccessView(new LoginOutputData(email));

            // Create and set session
            sessionRepository.setSession(new Session(
                    userDB.getUserId(),
                    userDB.getName(),
                    userDB.getEmail(),
                    System.currentTimeMillis()));
        }
        else {
            loginOutputPort.prepareFailView(new LoginOutputData(email, false, "Password doesn't match, please try again"));
        }
    }

    @Override
    public void switchView(String viewName) {
        loginOutputPort.switchView(viewName);
    }

    public String passwordHashing(String s) throws IllegalArgumentException, NoSuchAlgorithmException{
        if(s == null || s.isEmpty()) {
            throw new IllegalArgumentException("input can't be null");
        }

        // compute hash with SHA-1
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = sha.digest(s.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public boolean validateEmail(String email) {

        final String EMAIL_REGEX =
                "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String[] parts = email.split("@", -1);
        if (parts.length != 2) {
            return false;
        }
        String local = parts[0];
        String domain = parts[1];
        if (local.contains("..") || domain.contains("..")) {
            return false;
        }
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
