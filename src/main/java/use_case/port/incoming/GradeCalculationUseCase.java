package use_case.port.incoming;

import use_case.dto.GradeCalculationInputData;
import use_case.dto.GradeCalculationOutputData;

/**
 * Calculates target grades based on desired outcomes and existing marks.
 */
public interface GradeCalculationUseCase {
    GradeCalculationOutputData calculateTargets(GradeCalculationInputData request);
}
