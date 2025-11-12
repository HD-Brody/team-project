package use_case.service;

import entity.User;
import use_case.port.outgoing.LoginOutputBoundary;
import use_case.dto.LoginInputData;
import use_case.port.incoming.LoginInputBoundary;
import use_case.repository.UserRepository;

public class LoginInteractor implements LoginInputBoundary {

    private UserRepository userRepository;
    private LoginOutputBoundary loginOutputBoundary;

    public LoginInteractor(UserRepository userRepository, LoginOutputBoundary loginOutputBoundary) {
        this.userRepository = userRepository;
        this.loginOutputBoundary = loginOutputBoundary;
    }

    @Override
    public void execute(LoginInputData loginInputData) {
        final String username = loginInputData.getUsername();
        final String password = loginInputData.getPassword();

        String usernameDB;
        String passwordDB;
        User userDB = userRepository.getUserByUsername(username);
        usernameDB = userDB.getUserId();
        // TODO: get password hash based on user

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
