package use_case.dto;

import entity.AssessmentType;
import java.util.Objects;

/**
 * Parsed description of a grading scheme component before domain validation.
 */
public final class WeightComponentDraft {
    private final String name;
    private final AssessmentType type;
    private final double weight;
    private final Integer expectedCount;

    public WeightComponentDraft(String name, AssessmentType type, double weight, Integer expectedCount) {
        this.name = Objects.requireNonNull(name, "name");
        this.type = Objects.requireNonNull(type, "type");
        this.weight = weight;
        this.expectedCount = expectedCount;
    }

    public String getName() {
        return name;
    }

    public AssessmentType getType() {
        return type;
    }

    public double getWeight() {
        return weight;
    }

    public Integer getExpectedCount() {
        return expectedCount;
    }
}
