package interface_adapter.welcome;

import interface_adapter.ViewManagerModel;
import use_case.dto.WelcomeOutputData;
import use_case.port.outgoing.WelcomePort;
import view.WelcomeView;

public class WelcomePresenter implements WelcomePort {

    private final WelcomeViewModel welcomeViewModel;
    private final ViewManagerModel viewManagerModel;

    public WelcomePresenter(WelcomeViewModel welcomeViewModel, ViewManagerModel viewManagerModel) {
        this.welcomeViewModel = welcomeViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareFailView(WelcomeOutputData welcomeOutputData) {

    }

    @Override
    public void prepareSuccessView(WelcomeOutputData welcomeOutputData) {
        viewManagerModel.setState(welcomeOutputData.getActionType());
        viewManagerModel.firePropertyChange();
    }
}
