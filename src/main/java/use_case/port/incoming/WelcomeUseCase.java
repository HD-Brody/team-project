package use_case.port.incoming;

import use_case.dto.WelcomeInputData;

public interface WelcomeUseCase {

    void execute(WelcomeInputData welcomeInputData);
}
