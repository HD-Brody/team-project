package entity;

import java.util.Objects;

/**
 * Individual grading component declared in a marking scheme.
 */
public final class WeightComponent {
    private final String componentId;
    private final String schemeId;
    private final String name;
    private final AssessmentType type;
    private final double weight;
    private final Integer count;

    public WeightComponent(String componentId, String schemeId, String name,
                           AssessmentType type, double weight, Integer count) {
        this.componentId = Objects.requireNonNull(componentId, "componentId");
        this.schemeId = Objects.requireNonNull(schemeId, "schemeId");
        this.name = Objects.requireNonNull(name, "name");
        this.type = Objects.requireNonNull(type, "type");
        this.weight = weight;
        this.count = count;
    }

    public String getComponentId() {
        return componentId;
    }

    public String getSchemeId() {
        return schemeId;
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

    public Integer getCount() {
        return count;
    }
}
