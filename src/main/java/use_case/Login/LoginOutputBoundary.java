package use_case.Login;

public interface LoginOutputBoundary {

    void prepareSuccessView(LoginOutputData loginOutputData);

    void prepareFailView(LoginOutputData loginOutputData);
}
