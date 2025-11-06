package application.service;

import application.dto.GradeCalculationRequest;
import application.dto.GradeCalculationResponse;
import application.port.incoming.GradeCalculationUseCase;
import domain.repository.AssessmentRepository;
import domain.repository.GradeEntryRepository;
import domain.repository.MarkingSchemeRepository;
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
