package use_case.dto;

import entity.AssessmentType;
import java.util.Objects;

/**
 * Intermediate parsed representation of an assessment awaiting validation.
 */
public final class AssessmentDraft {
    private final String title;
    private final AssessmentType type;
    private final String dueDateIso;
    private final Double weight;
    private final String schemeComponentName;

    public AssessmentDraft(String title, AssessmentType type, String dueDateIso,
                           Double weight, String schemeComponentName) {
        this.title = Objects.requireNonNull(title, "title");
        this.type = Objects.requireNonNull(type, "type");
        this.dueDateIso = dueDateIso;
        this.weight = weight;
        this.schemeComponentName = schemeComponentName;
    }

    public String getTitle() {
        return title;
    }

    public AssessmentType getType() {
        return type;
    }

    public String getDueDateIso() {
        return dueDateIso;
    }

    public Double getWeight() {
        return weight;
    }

    public String getSchemeComponentName() {
        return schemeComponentName;
    }
}
