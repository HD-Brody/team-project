package interface_adapter.outbound.sign_up;

import use_case.dto.SignUpOutputData;
import use_case.port.outgoing.SignUpPort;

public class SignUpPresenter implements SignUpPort {

    @Override
    public void prepareSuccessView(SignUpOutputData signUpOutputData) {
        return;
    }

    @Override
    public void prepareFailView(SignUpOutputData signUpOutputData) {
        return;
    }
}
