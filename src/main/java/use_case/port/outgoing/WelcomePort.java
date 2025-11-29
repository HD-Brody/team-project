package use_case.port.outgoing;

import use_case.dto.WelcomeOutputData;

public interface WelcomePort {

    void prepareSuccessView(WelcomeOutputData welcomeOutputData);

    void prepareFailView(WelcomeOutputData welcomeOutputData);
}
