package interface_adapter.sign_up;

import interface_adapter.ViewModel;

public class SignUpViewModel extends ViewModel<SignUpState> {

    public SignUpViewModel() {
        super("signup");
        setState(new SignUpState());
    }
}
