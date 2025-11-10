package entity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the grading scheme defined in a syllabus.
 */
public final class MarkingScheme {
    private final String schemeId;
    private final String courseId;
    private final List<WeightComponent> components;

    public MarkingScheme(String schemeId, String courseId, List<WeightComponent> components) {
        this.schemeId = Objects.requireNonNull(schemeId, "schemeId");
        this.courseId = Objects.requireNonNull(courseId, "courseId");
        this.components = List.copyOf(Objects.requireNonNull(components, "components"));
    }

    public String getSchemeId() {
        return schemeId;
    }

    public String getCourseId() {
        return courseId;
    }

    public List<WeightComponent> getComponents() {
        return Collections.unmodifiableList(components);
    }
}
