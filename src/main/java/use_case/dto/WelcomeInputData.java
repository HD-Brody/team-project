package use_case.dto;

import interface_adapter.welcome.ActionType;

public class WelcomeInputData {

    private ActionType actionType;

    public WelcomeInputData(ActionType actionType) {
        this.actionType = actionType;
    }

    public ActionType getActionType() {
        return actionType;
    }
}