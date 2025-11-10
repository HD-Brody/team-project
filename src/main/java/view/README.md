# view

User-facing entrypoints (CLI, desktop, future UI shells) that interact with `interface_adapter` controllers.

## Contents
- `cli/Main.java` — temporary launcher providing a runnable stub (`mvn exec:java`).
- Additional UI layers (Swing, web, etc.) should live in dedicated subdirectories.

## Guidelines
- Keep logic minimal: gather input, call `interface_adapter` controllers, render responses.
- Avoid direct dependencies on `use_case` services; always route through adapters to preserve boundaries.
