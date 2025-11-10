# entity

Pure domain model layer: aggregates, value objects, enums, and domain-scoped exceptions.

## Contents
- Core aggregates such as `Course`, `Task`, `Assessment`, `Syllabus`.
- Supporting types: `AssessmentType`, `TaskStatus`, `SourceKind`, etc.
- `exception/` namespace with `DomainException` hierarchy.
- `service/` for stateless domain policies when a behavior spans multiple entities.

## Guidelines
- Keep classes framework-agnostic and immutable when feasible.
- Validate invariants inside constructors/factories; surface issues via `DomainException`.
- Do not reference `use_case`, `interface_adapter`, `data_access`, or `view` classes.
