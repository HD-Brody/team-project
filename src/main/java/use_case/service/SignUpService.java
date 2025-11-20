package use_case.service;

import use_case.dto.SignUpInputData;
import use_case.dto.SignUpOutputData;
import use_case.port.incoming.SignUpUseCase;
import use_case.port.outgoing.SignUpPort;
import use_case.repository.SignUpRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TimeZone;
import java.util.UUID;

public class SignUpService implements SignUpUseCase {

    private final SignUpRepository signUpRepository;
    private final SignUpPort signUpPort;

    public SignUpService(SignUpRepository signUpRepository, SignUpPort signUpPort) {
        this.signUpRepository = signUpRepository;
        this.signUpPort = signUpPort;
    }

    @Override
    public void execute(SignUpInputData signUpInputData) {
        String email = signUpInputData.getEmail();
        String password = signUpInputData.getPassword();
        String name = signUpInputData.getNickname();

        UUID uuid = UUID.randomUUID(); // user id

        if(password == null || password.isEmpty()) {
            signUpPort.prepareFailView(new SignUpOutputData(email, "Password is empty, please try again"));
        }
        else {
            String pwdHash;
            try {
                pwdHash = passwordHashing(password);
            }
            catch (Exception e) {
                signUpPort.prepareFailView(new SignUpOutputData(email, "Unknown Error occurred, please try again"));
                return;
            }

            signUpRepository.saveUser(
                    uuid.toString(),
                    name,
                    email,
                    TimeZone.getDefault().getID(),
                    pwdHash);
            signUpPort.prepareSuccessView(new SignUpOutputData(email, "success"));
        }

    }

    public String passwordHashing(String s) throws IllegalArgumentException, NoSuchAlgorithmException {
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
