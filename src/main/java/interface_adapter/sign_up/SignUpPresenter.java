package interface_adapter.sign_up;

import interface_adapter.ViewManagerModel;
import use_case.dto.SignUpOutputData;
import use_case.port.outgoing.SignUpPort;

public class SignUpPresenter implements SignUpPort {

    private final SignUpViewModel signUpViewModel;
    private final ViewManagerModel viewManagerModel;

    public SignUpPresenter(ViewManagerModel viewManagerModel, SignUpViewModel signUpViewModel) {
        this.signUpViewModel = signUpViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(SignUpOutputData signUpOutputData) {
        viewManagerModel.setState("welcome");
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(SignUpOutputData signUpOutputData) {
        SignUpState state = new SignUpState();
        state.setEmail(signUpOutputData.getEmail());
        state.setErrorMessage(signUpOutputData.getMessage());
        state.setIsSuccess(signUpOutputData.getIsSuccess());
        signUpViewModel.setState(state);
        signUpViewModel.firePropertyChange();
    }
}
