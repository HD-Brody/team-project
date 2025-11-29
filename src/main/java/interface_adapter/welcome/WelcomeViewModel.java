package interface_adapter.welcome;

import interface_adapter.ViewModel;

public class WelcomeViewModel extends ViewModel<WelcomeState> {


    public WelcomeViewModel() {
        super("welcome");
        setState(new WelcomeState());
    }
}
