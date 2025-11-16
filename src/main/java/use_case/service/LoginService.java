package use_case.service;

import entity.User;
import use_case.port.outgoing.LoginOutputPort;
import use_case.dto.LoginInputData;
import use_case.port.incoming.LoginUseCase;
import use_case.repository.LoginRepository;
import use_case.dto.LoginOutputData;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginService implements LoginUseCase {

    private final LoginRepository userRepository;
    private final LoginOutputPort loginOutputPort;

    public LoginService(LoginRepository loginRepository, LoginOutputPort loginOutputPort) {
        this.userRepository = loginRepository;
        this.loginOutputPort = loginOutputPort;
    }

    @Override
    public void execute(LoginInputData loginInputData) {
        final String username = loginInputData.getUsername();
        final String password = loginInputData.getPassword();

        String usernameDB;
        String passwordHashDB;
        User userDB = userRepository.getUserByUsername(username);

        if (userDB == null) {
            loginOutputPort.prepareFailView(new LoginOutputData(username, "User not found"));
            return;
        }

        usernameDB = userDB.getUserId();
        passwordHashDB = userRepository.getPasswordByUserID(username);

        String passwordHash = "";

        try {
            passwordHash = passwordHashing(password);
        }
        catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                loginOutputPort.prepareFailView(new LoginOutputData(username, "Password can't be empty"));
                return;
            }
            else {
                loginOutputPort.prepareFailView(new LoginOutputData(username, "Unexpected error, please try again"));
                return;
            }
        }

        if (username.equals(usernameDB) && passwordHash.equals(passwordHashDB)) {
            loginOutputPort.prepareSuccessView(new LoginOutputData(username));
        }
        else {
            loginOutputPort.prepareFailView(new LoginOutputData(username, "Password don't match, please try again"));
        }
        return;
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
}
