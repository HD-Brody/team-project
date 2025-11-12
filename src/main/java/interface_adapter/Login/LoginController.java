package interface_adapter.Login;

import use_case.dto.LoginInputData;
import use_case.service.LoginInteractor;

public class LoginController {

    private final LoginInteractor loginInteractor;

    public LoginController(LoginInteractor loginInteractor) {
        this.loginInteractor = loginInteractor;
    }

    public void execute(String username, String password) {
        final LoginInputData loginInputData = new LoginInputData(username, password);

        this.loginInteractor.execute(loginInputData);
        return;
    }
}
