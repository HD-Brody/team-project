package use_case.service;

import entity.User;
import use_case.dto.SignUpInputData;
import use_case.dto.SignUpOutputData;
import use_case.port.incoming.SignUpUseCase;
import use_case.port.outgoing.SignUpPort;
import use_case.repository.SignUpRepository;

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
        String username = signUpInputData.getUsername();
        String password = signUpInputData.getPassword();

        UUID uuid = UUID.randomUUID(); // replace to actual email in the future

        if(password == null || password.isEmpty()) {
            signUpPort.prepareFailView(new SignUpOutputData(username, "Password is empty, please try again"));
        }
        else {
            signUpRepository.saveUser(
                    username,
                    username,
                    uuid.toString(),
                    TimeZone.getDefault().getID(),
                    password);
            signUpPort.prepareSuccessView(new SignUpOutputData(username, "success"));
        }
        return;
    }
}
