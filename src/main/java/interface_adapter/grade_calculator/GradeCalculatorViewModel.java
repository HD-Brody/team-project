package interface_adapter.grade_calculator;

import interface_adapter.ViewModel;

public class GradeCalculatorViewModel extends ViewModel<GradeCalculatorState> {
    public GradeCalculatorViewModel() {
        super("grade_calculator");
        setState(new GradeCalculatorState());
    }
}
