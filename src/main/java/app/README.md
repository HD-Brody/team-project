# app

Bootstrap and configuration glue that wires the Clean Architecture layers together.

## Responsibilities
- Provide application-level configuration (Dependency Injection, env wiring, launchers).
- Define shared constants or factories consumed by `interface_adapter` or `use_case`.
- Maintain lightweight entrypoints that delegate to `view/` implementations.

## Current Status
- Placeholder directory: configuration modules land here once frameworks are selected.
