package interface_adapter.outbound.Login;

import use_case.port.outgoing.LoginOutputPort;
import use_case.dto.LoginOutputData;

public class LoginPresenter implements LoginOutputPort {

    @Override
    public void prepareSuccessView(LoginOutputData loginOutputData) {

        return;
    }

    @Override
    public void prepareFailView(LoginOutputData loginOutputData) {

        return;
    }
}
