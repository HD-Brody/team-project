package app;

import data_access.persistence.in_memory.InMemoryLoginInfoStorageDataAccessObject;
import data_access.persistence.in_memory.InMemorySessionInfoDataAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import use_case.dto.LoginInputData;
import use_case.port.incoming.LoginUseCase;
import use_case.port.outgoing.LoginOutputPort;
import use_case.service.LoginService;
import view.LoginView;
import view.ViewManager;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.Properties;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();
    final ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // data access obj, in memory for testing
    final InMemorySessionInfoDataAccessObject sessionDB = new InMemorySessionInfoDataAccessObject();
    final InMemoryLoginInfoStorageDataAccessObject userDB = new InMemoryLoginInfoStorageDataAccessObject();

    private LoginView loginView;
    private LoginViewModel loginViewModel;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel);
        cardPanel.add(loginView, loginView.getViewName());
        return this;
    }

    public AppBuilder addLoginUseCase() {
        final LoginOutputPort outputBoundary = new LoginPresenter(
                viewManagerModel,
                loginViewModel
        );

        final LoginUseCase interactor = new LoginService(
                userDB,
                sessionDB,
                outputBoundary
        );

        final LoginController controller = new LoginController(interactor);
        loginView.setLoginController(controller);
        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("Login");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.add(cardPanel);

        viewManagerModel.setState(loginView.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }
}