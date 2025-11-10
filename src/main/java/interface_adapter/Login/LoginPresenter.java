package interface_adapter.Login;

import use_case.Login.LoginOutputBoundary;
import use_case.Login.LoginOutputData;

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
