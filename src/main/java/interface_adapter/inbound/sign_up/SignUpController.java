package interface_adapter.inbound.sign_up;

import use_case.dto.SignUpInputData;
import use_case.port.incoming.SignUpUseCase;

public class SignUpController {
    private SignUpUseCase signUpUseCase;

    public SignUpController(SignUpUseCase signUpUseCase) {
        this.signUpUseCase = signUpUseCase;
    }

    public void execute(String username, String password) {
        signUpUseCase.execute(new SignUpInputData(username, password));
    }
}
