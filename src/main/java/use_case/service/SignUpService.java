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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        if(!validateEmail(email)) { // email validation
            signUpPort.prepareFailView(new SignUpOutputData(email, false, "Email is invalid, please try again"));
            return;
        }
        if (password == null || password.isEmpty()) { // password validation
            signUpPort.prepareFailView(new SignUpOutputData(email, false, "Password is empty, please try again"));
            return;
        }
        if (name == null || name.isEmpty()) {
            signUpPort.prepareFailView(new SignUpOutputData(email, false, "Name is empty, please try again"));
            return;
        }
        else {
            String pwdHash;
            try {
                pwdHash = passwordHashing(password);
            }
            catch (Exception e) {
                signUpPort.prepareFailView(new SignUpOutputData(email, false, "Unknown Error occurred, please try again"));
                return;
            }

            try{
                signUpRepository.saveUser(
                        uuid.toString(),
                        name,
                        email,
                        TimeZone.getDefault().getID(),
                        pwdHash);
            }
            catch (Exception e) {
                signUpPort.prepareFailView(new SignUpOutputData(email, false, "Name has been used, please try again"));
                return;
            }
            signUpPort.prepareSuccessView(new SignUpOutputData(email, true, "success"));
        }

    }

    @Override
    public void switchView() {
        signUpPort.prepareSuccessView(new SignUpOutputData("", true, ""));
    }

    public String passwordHashing(String s) throws IllegalArgumentException, NoSuchAlgorithmException {
        if (s == null || s.isEmpty()) {
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
