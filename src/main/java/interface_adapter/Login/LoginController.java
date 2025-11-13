package interface_adapter.Login;

import use_case.dto.LoginInputData;
import use_case.service.LoginService;

public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    public void execute(String username, String password) {
        final LoginInputData loginInputData = new LoginInputData(username, password);

        this.loginService.execute(loginInputData);
        return;
    }
}
