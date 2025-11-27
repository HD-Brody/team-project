package use_case.dto;

import interface_adapter.welcome.ActionType;

public class WelcomeOutputData {

    String actionType;

    public WelcomeOutputData(String actionType) {
        this.actionType = actionType;
    }

    public String getActionType() {
        return actionType;
    }
}
