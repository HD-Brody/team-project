package app;

import data_access.persistence.in_memory.InMemoryLoginInfoStorageDataAccessObject;
import data_access.persistence.in_memory.InMemorySessionInfoDataAccessObject;
import data_access.persistence.sqlite.Login;
import data_access.persistence.sqlite.Signup;
import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.welcome.WelcomeController;
import interface_adapter.welcome.WelcomePresenter;
import interface_adapter.welcome.WelcomeViewModel;
import use_case.dto.LoginInputData;
import use_case.dto.WelcomeOutputData;
import use_case.port.incoming.LoginUseCase;
import use_case.port.incoming.WelcomeUseCase;
import use_case.port.outgoing.*;
import use_case.repository.*;
import use_case.service.LoginService;
import use_case.service.WelcomeService;
import view.*;
import data_access.persistence.in_memory.InMemorySignUpDataAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.sign_up.SignUpController;
import interface_adapter.sign_up.SignUpPresenter;
import interface_adapter.sign_up.SignUpViewModel;
import use_case.port.incoming.SignUpUseCase;
import use_case.service.SignUpService;

import javax.swing.*;
import java.awt.*;
import data_access.ai.gemini.AiExtractorDataAccessObject;
import data_access.parser.pdf.PdfExtractorDataAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.syllabus_upload.SyllabusUploadController;
import interface_adapter.syllabus_upload.SyllabusUploadPresenter;
import interface_adapter.syllabus_upload.SyllabusUploadViewModel;
import use_case.port.incoming.UploadSyllabusInputBoundary;
import use_case.service.SyllabusUploadInteractor;
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

    final InMemorySessionInfoDataAccessObject sessionDB = new InMemorySessionInfoDataAccessObject();
    final LoginRepository userDB = new Login();

    private LoginView loginView;
    private LoginViewModel loginViewModel;
    // Data Access Objects
    private final PdfExtractionDataAccessInterface pdfExtractor = new PdfExtractorDataAccessObject();
    private final AiExtractionDataAccessInterface aiExtractor;
    final SignUpRepository signUpDB = new Signup();
    
    // Repositories - Using IN-MEMORY implementations for testing
    private final SyllabusRepository syllabusRepository = new InMemorySyllabusRepository();
    private final AssessmentRepository assessmentRepository = new InMemoryAssessmentRepository();
    private final CourseRepository courseRepository = new InMemoryCourseRepository();
    
    // Views
    private SyllabusUploadView syllabusUploadView;
    private SyllabusUploadViewModel syllabusUploadViewModel;
      
    private SignUpView signUpView;
    private SignUpViewModel signUpViewModel;

    private WelcomeView welcomeView;
    private WelcomeViewModel welcomeViewModel;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
        
        // Load Gemini API key from config
        String apiKey = loadApiKey();
        aiExtractor = new AiExtractorDataAccessObject(apiKey);
    }

    private String loadApiKey() {
        Properties config = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found in resources folder");
            }
            config.load(input);
            String key = config.getProperty("gemini.api.key");
            if (key == null || key.trim().isEmpty()) {
                throw new RuntimeException("gemini.api.key not found in config.properties");
            }
            return key;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load API key: " + e.getMessage(), e);
        }
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
                signUpDB,
                outputBoundary
        );

        final SignUpController controller = new SignUpController(interactor);
        signUpView.setSignUpController(controller);
        return this;
    }

    public AppBuilder addSyllabusUploadView() {
        syllabusUploadViewModel = new SyllabusUploadViewModel();
        syllabusUploadView = new SyllabusUploadView(syllabusUploadViewModel);
        cardPanel.add(syllabusUploadView, syllabusUploadView.getViewName());
        return this;
    }

    public AppBuilder addSyllabusUploadUseCase() {
        final SyllabusUploadOutputBoundary outputBoundary = new SyllabusUploadPresenter(
            viewManagerModel,
            syllabusUploadViewModel
        );
        
        final UploadSyllabusInputBoundary interactor = new SyllabusUploadInteractor(
            pdfExtractor,
            aiExtractor,
            courseRepository,
            syllabusRepository,
            assessmentRepository,
            outputBoundary
        );

        final SyllabusUploadController controller = new SyllabusUploadController(interactor);
        syllabusUploadView.setSyllabusUploadController(controller);
        return this;
    }

    public AppBuilder addWelcomeView() {
        welcomeViewModel = new WelcomeViewModel();
        welcomeView = new WelcomeView(welcomeViewModel);
        cardPanel.add(welcomeView, welcomeView.getViewName());
        return this;
    }

    public AppBuilder addWelcomeUseCase() {
        final WelcomePort port = new WelcomePresenter(welcomeViewModel, viewManagerModel);
        final WelcomeUseCase useCase = new WelcomeService(port);
        final WelcomeController welcomeController = new WelcomeController(useCase);
        welcomeView.setWelcomeViewController(welcomeController);
        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("Time Til Test");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.add(cardPanel);

        viewManagerModel.setState(welcomeView.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }
}