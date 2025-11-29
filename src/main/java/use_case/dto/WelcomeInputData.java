package use_case.dto;

import interface_adapter.welcome.ActionType;

public class WelcomeInputData {

    private final String actionType;

    public WelcomeInputData(String actionType) {
        this.actionType = actionType;
    }

    public String getActionType() {
        return actionType;
    }
}