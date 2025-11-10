# data_access

Concrete gateways that talk to infrastructure concerns (databases, files, external APIs).

## Subpackages
- `persistence/` — e.g., `persistence/sqlite` for repositories and transaction wiring.
- `parser/` — syllabus parsing helpers such as PDF extractors.
- `ai/` — ML/NLP integrations (Gemini, etc.).
- `calendar/` — outbound calendar publishers (Google, ICS writers).
- `config/` — adapters that surface secrets or env configuration safely.

## Guidelines
- Keep dependencies pointing inward toward `use_case` ports.
- Avoid leaking framework types upstream; translate data into `entity` models before returning.
