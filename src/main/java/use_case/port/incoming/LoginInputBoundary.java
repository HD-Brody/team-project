package use_case.port.incoming;

import use_case.dto.LoginInputData;

public interface LoginInputBoundary {

    void execute(LoginInputData loginInputData);
}
