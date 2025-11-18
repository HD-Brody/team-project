package interface_adapter.inbound.sign_up;

import use_case.dto.SignUpInputData;
import use_case.port.incoming.SignUpUseCase;

public class SignUpController {
    private final SignUpUseCase signUpUseCase;

    public SignUpController(SignUpUseCase signUpUseCase) {
        this.signUpUseCase = signUpUseCase;
    }

    public void execute(String email, String password, String nickname) {
        signUpUseCase.execute(new SignUpInputData(email, password, nickname));
    }
}
