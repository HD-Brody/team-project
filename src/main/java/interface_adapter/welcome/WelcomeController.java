package interface_adapter.welcome;

import use_case.dto.WelcomeInputData;
import use_case.port.incoming.WelcomeUseCase;

public class WelcomeController {

    private final WelcomeUseCase welcomeUseCase;

    public WelcomeController(WelcomeUseCase welcomeUseCase) {
        this.welcomeUseCase = welcomeUseCase;
    }

    public void execute(ActionType actionType) {
        welcomeUseCase.execute(new WelcomeInputData(actionType));
    }
}
