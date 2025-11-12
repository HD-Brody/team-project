package interface_adapter.Login;

import use_case.port.outgoing.LoginOutputBoundary;
import use_case.dto.LoginOutputData;

public class LoginPresenter implements LoginOutputBoundary {

    @Override
    public void prepareSuccessView(LoginOutputData loginOutputData) {
        return;
    }

    @Override
    public void prepareFailView(LoginOutputData loginOutputData) {
        return;
    }
}
