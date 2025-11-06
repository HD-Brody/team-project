package application.port.incoming;

import application.dto.GradeCalculationRequest;
import application.dto.GradeCalculationResponse;

/**
 * Calculates target grades based on desired outcomes and existing marks.
 */
public interface GradeCalculationUseCase {
    GradeCalculationResponse calculateTargets(GradeCalculationRequest request);
}
