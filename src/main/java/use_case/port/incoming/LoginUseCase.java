package use_case.port.incoming;

import use_case.dto.LoginInputData;

public interface LoginUseCase {

    void execute(LoginInputData loginInputData);

    void switchView(String viewName);
}
