package use_case.service;

import use_case.dto.SignUpInputData;
import use_case.dto.SignUpOutputData;
import use_case.port.incoming.SignUpUseCase;
import use_case.port.outgoing.SignUpPort;
import use_case.repository.SignUpRepository;
import use_case.util.HashUtil;
import use_case.util.ValidationUtil;

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

        if(!ValidationUtil.validateEmail(email)) { // email validation
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
                pwdHash = HashUtil.hash(password);
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

}
