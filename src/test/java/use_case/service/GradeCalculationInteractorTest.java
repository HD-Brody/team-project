package use_case.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.Assessment;
import entity.AssessmentType;
import java.util.List;
import org.junit.jupiter.api.Test;
import use_case.dto.GradeCalculationInputData;
import use_case.dto.GradeCalculationOutputData;

class GradeCalculationInteractorTest {

    @Test
    void handlesNoAssessments() {
        GradeCalculationOutputData response = calculate(List.of(), 0);

        assertEquals(0.0, response.getProjectedPercent());
        assertEquals(3, response.getRequiredScores().size());
        assertEquals("BEST_CASE_PERCENT", response.getRequiredScores().get(0).getTitle());
        assertEquals(0.0, response.getRequiredScores().get(0).getGrade());
        assertEquals("WORST_CASE_PERCENT", response.getRequiredScores().get(1).getTitle());
        assertEquals(0.0, response.getRequiredScores().get(1).getGrade());
        assertEquals("CURRENT_PERCENT", response.getRequiredScores().get(2).getTitle());
        assertEquals(0.0, response.getRequiredScores().get(2).getGrade());
    }

    @Test
    void calculatesWhenAllGradesPresent() {
        List<Assessment> assessments = List.of(
                assessment("a1", 0.4, 80),
                assessment("a2", 0.6, 90)
        );

        GradeCalculationOutputData response = calculate(assessments, 0);

        assertEquals(86.0, response.getProjectedPercent(), 1e-9);
        assertEquals(3, response.getRequiredScores().size());
        assertEquals(86.0, response.getRequiredScores().get(0).getGrade(), 1e-9);
        assertEquals(86.0, response.getRequiredScores().get(1).getGrade(), 1e-9);
        assertEquals(86.0, response.getRequiredScores().get(2).getGrade(), 1e-9);
    }

    @Test
    void calculatesRequiredAverageForRemainingAssessments() {
        List<Assessment> assessments = List.of(
                assessment("a1", 0.3, 70),
                assessment("a2", 0.4, -1),
                assessment("a3", 0.3, -1)
        );

        GradeCalculationOutputData response = calculate(assessments, 80);

        double expectedRequiredAverage = (80 * 1.0 - 0.3 * 70) / 0.7;
        assertEquals(70.0, response.getProjectedPercent(), 1e-9);
        assertEquals(6, response.getRequiredScores().size());
        assertEquals("REQUIRED_AVERAGE_REMAINING", response.getRequiredScores().get(3).getTitle());
        assertEquals(expectedRequiredAverage, response.getRequiredScores().get(3).getGrade(), 1e-9);
        assertEquals(expectedRequiredAverage, response.getRequiredScores().get(4).getGrade(), 1e-9);
        assertEquals(expectedRequiredAverage, response.getRequiredScores().get(5).getGrade(), 1e-9);
    }

    @Test
    void targetExactlyAtPerfectScores() {
        List<Assessment> assessments = List.of(
                assessment("midterm", 0.5, 80),
                assessment("final", 0.5, -1)
        );

        GradeCalculationOutputData response = calculate(assessments, 90);

        assertEquals(80.0, response.getProjectedPercent(), 1e-9);
        assertEquals(5, response.getRequiredScores().size());
        assertEquals(100.0, response.getRequiredScores().get(3).getGrade(), 1e-9);
        assertEquals(100.0, response.getRequiredScores().get(4).getGrade(), 1e-9);
    }

    @Test
    void flagsTargetAsImpossibleWhenOverHundred() {
        List<Assessment> assessments = List.of(
                assessment("quiz", 0.6, 50),
                assessment("exam", 0.4, -1)
        );

        GradeCalculationOutputData response = calculate(assessments, 90);

        double requiredAverage = (90 - 0.6 * 50) / 0.4;
        assertEquals(5, response.getRequiredScores().size());
        assertEquals(requiredAverage, response.getRequiredScores().get(3).getGrade(), 1e-9);
        assertTrue(requiredAverage > 100.0);
    }

    @Test
    void identifiesTargetAsGuaranteedWhenNegativeAverageNeeded() {
        List<Assessment> assessments = List.of(
                assessment("a1", 0.8, 95),
                assessment("a2", 0.2, -1)
        );

        GradeCalculationOutputData response = calculate(assessments, 60);

        double requiredAverage = (60 - 0.8 * 95) / 0.2;
        assertEquals(5, response.getRequiredScores().size());
        assertEquals(requiredAverage, response.getRequiredScores().get(3).getGrade(), 1e-9);
        assertTrue(requiredAverage < 0.0);
    }

    private GradeCalculationOutputData calculate(List<Assessment> assessments, double targetPercent) {
        GradeCalculationInteractor service = new GradeCalculationInteractor(assessments);
        GradeCalculationInputData request = new GradeCalculationInputData("CSC207", "user-1", targetPercent, assessments);
        return service.calculateTargets(request);
    }

    private Assessment assessment(String id, double weight, double grade) {
        return new Assessment(
                id,
                "CSC207",
                id,
                AssessmentType.ASSIGNMENT,
                grade,
                null,
                null,
                null,
                weight,
                null,
                null
        );
    }
}
