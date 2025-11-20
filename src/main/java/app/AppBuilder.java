package app;

import data_access.ai.gemini.AiExtractorDataAccessObject;
import data_access.parser.pdf.PdfExtractorDataAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.syllabus_upload.SyllabusUploadController;
import interface_adapter.syllabus_upload.SyllabusUploadPresenter;
import interface_adapter.syllabus_upload.SyllabusUploadViewModel;
import use_case.port.incoming.UploadSyllabusInputBoundary;
import use_case.port.outgoing.AiExtractionDataAccessInterface;
import use_case.port.outgoing.PdfExtractionDataAccessInterface;
import use_case.port.outgoing.SyllabusUploadOutputBoundary;
import use_case.repository.AssessmentRepository;
import use_case.repository.CourseRepository;
import use_case.repository.SyllabusRepository;
import use_case.repository.InMemoryAssessmentRepository;
import use_case.repository.InMemoryCourseRepository;
import use_case.repository.InMemorySyllabusRepository;
import use_case.service.SyllabusUploadInteractor;
import view.ViewManager;
import view.SyllabusUploadView;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.Properties;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();
    final ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // Data Access Objects
    private final PdfExtractionDataAccessInterface pdfExtractor = new PdfExtractorDataAccessObject();
    private final AiExtractionDataAccessInterface aiExtractor;
    
    // Repositories - Using IN-MEMORY implementations for testing
    private final SyllabusRepository syllabusRepository = new InMemorySyllabusRepository();
    private final AssessmentRepository assessmentRepository = new InMemoryAssessmentRepository();
    private final CourseRepository courseRepository = new InMemoryCourseRepository();
    
    // Views
    private SyllabusUploadView syllabusUploadView;
    private SyllabusUploadViewModel syllabusUploadViewModel;

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

    public JFrame build() {
        final JFrame application = new JFrame("Course Assessment Manager");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.add(cardPanel);

        viewManagerModel.setState(syllabusUploadView.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }
}