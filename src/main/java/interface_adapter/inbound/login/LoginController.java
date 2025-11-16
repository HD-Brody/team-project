package interface_adapter.inbound.login;

import use_case.dto.LoginInputData;
import use_case.port.incoming.LoginUseCase;

public class LoginController {

    private final LoginUseCase loginService;

    public LoginController(LoginUseCase loginService) {
        this.loginService = loginService;
    }

    public void execute(String username, String password) {
        final LoginInputData loginInputData = new LoginInputData(username, password);

        this.loginService.execute(loginInputData);
        return;
    }
}
