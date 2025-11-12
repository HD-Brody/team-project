package use_case.service;

import use_case.port.outgoing.LoginOutputBoundary;
import use_case.dto.LoginInputData;
import use_case.port.incoming.LoginInputBoundary;

public class LoginInteractor implements LoginInputBoundary {

    // TODO: reference of DAO
    LoginOutputBoundary loginOutputBoundary;

    @Override
    public void execute(LoginInputData loginInputData) {
        final String username = loginInputData.getUsername();
        final String password = loginInputData.getPassword();

        // TODO: get username and password from DB
//        String usernameDB;
//        String passwordDB;
//
//        if (username == usernameDB && password == passwordDB) {
//            loginOutputBoundary.prepareSuccessView(new LoginOutputData(username));
//        }
//        else {
//            // TODO: show error message and reset login view
//            loginOutputBoundary.prepareFailView(new LoginOutputData(username));
//        }
        return;
    }
}
