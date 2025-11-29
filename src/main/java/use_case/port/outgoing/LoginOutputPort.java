package use_case.port.outgoing;

import use_case.dto.LoginOutputData;

public interface LoginOutputPort {

    void prepareSuccessView(LoginOutputData loginOutputData);

    void prepareFailView(LoginOutputData loginOutputData);

    void switchView(String viewName);
}
