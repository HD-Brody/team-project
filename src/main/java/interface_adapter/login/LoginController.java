package interface_adapter.login;

import use_case.dto.LoginInputData;
import use_case.port.incoming.LoginUseCase;

public class LoginController {

    private final LoginUseCase loginService;

    public LoginController(LoginUseCase loginService) {
        this.loginService = loginService;
    }

    public void execute(String email, String password) {
        final LoginInputData loginInputData = new LoginInputData(email, password);

        this.loginService.execute(loginInputData);
        return;
    }
}
