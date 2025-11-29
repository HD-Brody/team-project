package interface_adapter.login;

import interface_adapter.ViewManagerModel;
import interface_adapter.dashboard.DashboardController;
import use_case.port.outgoing.LoginOutputPort;
import use_case.dto.LoginOutputData;

public class LoginPresenter implements LoginOutputPort {

    private final LoginViewModel loginViewModel;
    private final ViewManagerModel viewManagerModel;
    private final DashboardController dashboardController;

    public LoginPresenter(ViewManagerModel viewManagerModel,
                         LoginViewModel loginViewModel,
                         DashboardController dashboardController) {
        this.loginViewModel = loginViewModel;
        this.viewManagerModel = viewManagerModel;
        this.dashboardController = dashboardController;
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

        // Load dashboard with the correct user ID
        if (loginOutputData.getUserId() != null) {
            dashboardController.loadDashboard(loginOutputData.getUserId());
        }

        // Navigate to dashboard on successful login
        viewManagerModel.setState("dashboard");
        viewManagerModel.firePropertyChange();
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
