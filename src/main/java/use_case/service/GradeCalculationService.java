package use_case.service;

import use_case.dto.GradeCalculationRequest;
import use_case.dto.GradeCalculationResponse;
import use_case.port.incoming.GradeCalculationUseCase;
import use_case.repository.AssessmentRepository;
import use_case.repository.GradeEntryRepository;
import use_case.repository.MarkingSchemeRepository;
import java.util.Objects;

/**
 * Provides weighted grade projections for a course.
 */
public class GradeCalculationService implements GradeCalculationUseCase {
    private final AssessmentRepository assessmentRepository;
    private final GradeEntryRepository gradeEntryRepository;
    private final MarkingSchemeRepository markingSchemeRepository;

    public GradeCalculationService(AssessmentRepository assessmentRepository,
                                   GradeEntryRepository gradeEntryRepository,
                                   MarkingSchemeRepository markingSchemeRepository) {
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository,
                "assessmentRepository");
        this.gradeEntryRepository = Objects.requireNonNull(gradeEntryRepository,
                "gradeEntryRepository");
        this.markingSchemeRepository = Objects.requireNonNull(markingSchemeRepository,
                "markingSchemeRepository");
    }

    @Override
    public GradeCalculationResponse calculateTargets(GradeCalculationRequest request) {
        // TODO: implement weighted grade calculation logic.
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
