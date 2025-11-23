package app;

import data_access.persistence.in_memory.InMemorySignUpDataAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.sign_up.SignUpController;
import interface_adapter.sign_up.SignUpPresenter;
import interface_adapter.sign_up.SignUpViewModel;
import use_case.port.incoming.SignUpUseCase;
import use_case.port.outgoing.SignUpPort;
import use_case.service.SignUpService;
import view.SignUpView;
import view.ViewManager;

import javax.swing.*;
import java.awt.*;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();
    final ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // data access obj, in memory for testing
    final InMemorySignUpDataAccessObject userDB = new InMemorySignUpDataAccessObject();

    private SignUpView signUpView;
    private SignUpViewModel signUpViewModel;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addSignUpView() {
        signUpViewModel = new SignUpViewModel();
        signUpView = new SignUpView(signUpViewModel);
        cardPanel.add(signUpView, signUpView.getViewName());
        return this;
    }

    public AppBuilder addSignUpUseCase() {
        final SignUpPort outputBoundary = new SignUpPresenter(
                viewManagerModel,
                signUpViewModel
        );

        final SignUpUseCase interactor = new SignUpService(
                userDB,
                outputBoundary
        );

        final SignUpController controller = new SignUpController(interactor);
        signUpView.setSignUpController(controller);
        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("Sign Up");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.add(cardPanel);

        viewManagerModel.setState(signUpView.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }
}