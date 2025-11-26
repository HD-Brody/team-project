package use_case.service;

import use_case.dto.WelcomeInputData;
import use_case.dto.WelcomeOutputData;
import use_case.port.incoming.WelcomeUseCase;
import use_case.port.outgoing.WelcomePort;

public class WelcomeService implements WelcomeUseCase {

    private final WelcomePort welcomePort;

    public WelcomeService(WelcomePort welcomePort) {
        this.welcomePort = welcomePort;
    }

    public void execute(WelcomeInputData welcomeInputData) {
        welcomePort.prepareSuccessView(new WelcomeOutputData(welcomeInputData.getActionType()));
    }
}
