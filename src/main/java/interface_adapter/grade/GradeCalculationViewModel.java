package interface_adapter.grade;

import interface_adapter.ViewModel;

/**
 * View model for grade calculation UI.
 */
public class GradeCalculationViewModel extends ViewModel<GradeCalculationState> {

    public GradeCalculationViewModel() {
        super("grade_calculation");
        setState(new GradeCalculationState());
    }
}
