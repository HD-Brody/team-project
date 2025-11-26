package app;

import data_access.ai.gemini.AiExtractorDataAccessObject;
import data_access.parser.pdf.PdfExtractorDataAccessObject;
import data_access.persistence.in_memory.InMemoryLoginInfoStorageDataAccessObject;
import data_access.persistence.in_memory.InMemorySessionInfoDataAccessObject;
import data_access.persistence.in_memory.InMemorySignUpDataAccessObject;
import data_access.persistence.sqlite.Course;
import data_access.persistence.sqlite.Syllabus;
import data_access.persistence.sqlite.Assessment;
import interface_adapter.ViewManagerModel;
import interface_adapter.dashboard.DashboardController;
import interface_adapter.dashboard.DashboardPresenter;
import interface_adapter.dashboard.DashboardViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.sign_up.SignUpController;
import interface_adapter.sign_up.SignUpPresenter;
import interface_adapter.sign_up.SignUpViewModel;
import interface_adapter.syllabus_upload.SyllabusUploadController;
import interface_adapter.syllabus_upload.SyllabusUploadPresenter;
import interface_adapter.syllabus_upload.SyllabusUploadViewModel;
import use_case.port.incoming.LoadDashboardInputBoundary;
import use_case.port.incoming.LoginUseCase;
import use_case.port.incoming.SignUpUseCase;
import use_case.port.incoming.UploadSyllabusInputBoundary;
import use_case.port.outgoing.AiExtractionDataAccessInterface;
import use_case.port.outgoing.LoadDashboardOutputBoundary;
import use_case.port.outgoing.LoginOutputPort;
import use_case.port.outgoing.PdfExtractionDataAccessInterface;
import use_case.port.outgoing.SignUpPort;
import use_case.port.outgoing.SyllabusUploadOutputBoundary;
import use_case.repository.AssessmentRepository;
import use_case.repository.CourseRepository;
import use_case.repository.InMemoryAssessmentRepository;
import use_case.repository.InMemoryCourseRepository;
import use_case.repository.InMemorySyllabusRepository;
import use_case.repository.SyllabusRepository;
import use_case.service.LoadDashboardInteractor;
import use_case.service.LoginService;
import use_case.service.SignUpService;
import use_case.service.SyllabusUploadInteractor;
import view.DashboardView;
import view.LoginView;
import view.SignUpView;
import view.SyllabusUploadView;
import view.ViewManager;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.Properties;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();
    private final ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // Data Access Objects
    private final InMemorySessionInfoDataAccessObject sessionDB = new InMemorySessionInfoDataAccessObject();
    private final InMemoryLoginInfoStorageDataAccessObject userDB = new InMemoryLoginInfoStorageDataAccessObject();
    private final InMemorySignUpDataAccessObject signUpDB = new InMemorySignUpDataAccessObject();
    private final PdfExtractionDataAccessInterface pdfExtractor = new PdfExtractorDataAccessObject();
    private final AiExtractionDataAccessInterface aiExtractor;
    
    // Repositories - Using SQLite implementations for persistence
    private final SyllabusRepository syllabusRepository = new data_access.persistence.sqlite.Syllabus();
    private final AssessmentRepository assessmentRepository = new data_access.persistence.sqlite.Assessment();
    private final CourseRepository courseRepository = new data_access.persistence.sqlite.Course();
    
    // Views
    private LoginView loginView;
    private LoginViewModel loginViewModel;
    private SyllabusUploadView syllabusUploadView;
    private SyllabusUploadViewModel syllabusUploadViewModel;
    private SignUpView signUpView;
    private SignUpViewModel signUpViewModel;
    private DashboardView dashboardView;
    private DashboardViewModel dashboardViewModel;

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
        syllabusUploadView = new SyllabusUploadView(syllabusUploadViewModel, viewManagerModel);
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

    public AppBuilder addDashboardView() {
        dashboardViewModel = new DashboardViewModel();
        dashboardView = new DashboardView(dashboardViewModel, viewManagerModel);
        cardPanel.add(dashboardView, dashboardView.getViewName());
        return this;
    }

    public AppBuilder addDashboardUseCase() {
        final LoadDashboardOutputBoundary outputBoundary = new DashboardPresenter(
            viewManagerModel,
            dashboardViewModel,
            syllabusUploadViewModel
        );
        
        final LoadDashboardInputBoundary interactor = new LoadDashboardInteractor(
            courseRepository,
            assessmentRepository,
            outputBoundary
        );

        final DashboardController controller = new DashboardController(interactor);
        dashboardView.setDashboardController(controller);
        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("Dashboard");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.add(cardPanel);

        viewManagerModel.setState(dashboardView.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }
}