package interface_adapter.login;

import interface_adapter.ViewManagerModel;
import use_case.port.outgoing.LoginOutputPort;
import use_case.dto.LoginOutputData;
import view.ViewManager;

public class LoginPresenter implements LoginOutputPort {

    private final LoginViewModel loginViewModel;
    private final ViewManagerModel viewManagerModel;

    public LoginPresenter(ViewManagerModel viewManagerModel, LoginViewModel loginViewModel) {
        this.loginViewModel = loginViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(LoginOutputData loginOutputData) {

        LoginState state = new LoginState();
        state.setEmail(loginOutputData.getEmail());
        state.setIsSuccess(loginOutputData.getIsSuccess());
        state.setErrorMessage(loginOutputData.getMessage());
        loginViewModel.setState(state);
        loginViewModel.firePropertyChange();

        System.out.println("login success");
        // TODO: dashboard implementation
//        viewManagerModel.setState("dashboard");
//        viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(LoginOutputData loginOutputData) {

        LoginState state = new LoginState();
        state.setEmail(loginOutputData.getEmail());
        state.setIsSuccess(loginOutputData.getIsSuccess());
        state.setErrorMessage(loginOutputData.getMessage());
        loginViewModel.setState(state);
        loginViewModel.firePropertyChange();
    }

    @Override
    public void switchView(String viewName) {
        viewManagerModel.setState(viewName);
        viewManagerModel.firePropertyChange();
    }
}
