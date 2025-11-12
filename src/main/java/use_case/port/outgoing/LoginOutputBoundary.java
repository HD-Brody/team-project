package use_case.port.outgoing;

import use_case.dto.LoginOutputData;

public interface LoginOutputBoundary {

    void prepareSuccessView(LoginOutputData loginOutputData);

    void prepareFailView(LoginOutputData loginOutputData);
}
