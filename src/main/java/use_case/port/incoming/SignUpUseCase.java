package use_case.port.incoming;

import use_case.dto.SignUpInputData;

public interface SignUpUseCase {

    void execute(SignUpInputData signUpInputData);

    void switchView();
}
