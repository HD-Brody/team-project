package use_case.dto;

import interface_adapter.welcome.ActionType;

public class WelcomeOutputData {

    ActionType actionType;

    public WelcomeOutputData(ActionType actionType) {
        this.actionType = actionType;
    }

    public ActionType getActionType() {
        return actionType;
    }
}
