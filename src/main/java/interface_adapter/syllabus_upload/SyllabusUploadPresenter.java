package interface_adapter.syllabus_upload;

import interface_adapter.ViewManagerModel;
import use_case.port.outgoing.SyllabusUploadOutputBoundary;
import use_case.dto.SyllabusUploadOutputData;

public class SyllabusUploadPresenter implements SyllabusUploadOutputBoundary {
    private final SyllabusUploadViewModel syllabusUploadViewModel;
    private final ViewManagerModel viewManagerModel;

    public SyllabusUploadPresenter(ViewManagerModel viewManagerModel,
                                   SyllabusUploadViewModel syllabusUploadViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.syllabusUploadViewModel = syllabusUploadViewModel;
    }

    @Override
    public void prepareSuccessView(SyllabusUploadOutputData outputData) {
        // Update the state to show success
        SyllabusUploadState state = syllabusUploadViewModel.getState();
        state.setProcessing(false);
        state.setError(null);
        syllabusUploadViewModel.setState(state);
        syllabusUploadViewModel.firePropertyChange();
        
        // TODO: When dashboard is implemented, switch to it
        // viewManagerModel.setState("dashboard");
        // viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        // Update the state to show the error
        SyllabusUploadState state = syllabusUploadViewModel.getState();
        state.setProcessing(false);
        state.setError(errorMessage);
        syllabusUploadViewModel.setState(state);
        syllabusUploadViewModel.firePropertyChange();
    }
}