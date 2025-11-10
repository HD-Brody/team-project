package use_case.port.incoming;

import use_case.dto.GradeCalculationRequest;
import use_case.dto.GradeCalculationResponse;

/**
 * Calculates target grades based on desired outcomes and existing marks.
 */
public interface GradeCalculationUseCase {
    GradeCalculationResponse calculateTargets(GradeCalculationRequest request);
}
