# Repository Structure Proposal

_Prepared on Nov 6, 2025 by Andy Chen, to explain how the existing skeleton should be extended during development._

## 1. Scope and Intent
This document provides a plain guide to the repository layout, describing how each directory supports the Syllabus Assistant project outlined in `doc/project-blueprint.md` and sequenced in `doc/timeline-proposal.md`. Use it as a reference when planning features, wiring services, or onboarding new contributors.

## 2. Root Layout
- `pom.xml` — Maven build descriptor for the single Java 11 module; keep dependencies and plugins aligned with the agreed tooling stack.
- `README.md` — Introductory project overview; extend with setup instructions once the first executable milestone is available.
- `doc/` — Team-facing documentation. Top-level files record the blueprint, timeline, and team contract.
- `src/` — All source code and tests. The subdirectories follow Clean Architecture boundaries and mirror the planned workstreams.
- `target/` — Maven build output (compiled classes, packaged JARs). Generated artifacts live here temporarily and must stay untracked.

## 3. `src/main/java` Packages
- `adapter/` — Inbound and outbound adapters that translate between the external world and the application core. Use `adapter/inbound/web` for controllers or handlers, `adapter/inbound/cli` for command-line wiring, and `adapter/outbound/*` for integrations such as calendar publishing, AI parsing, and persistence gateways.
- `application/` — Use cases, DTOs, and ports that coordinate domain logic. Services in `application/service` call repositories and adapters through interfaces defined under `application/port/incoming` and `application/port/outgoing`.
- `domain/` — Pure business logic. Place aggregates and value objects in `domain/model`, shared policies or validators in `domain/service`, and repository interfaces in `domain/repository`.
- `infrastructure/` — Technical implementations that satisfy outbound ports. Subpackages mirror the adapter needs: `infrastructure/persistence/sqlite`, `infrastructure/parser/pdf`, `infrastructure/ai/gemini`, `infrastructure/calendar/google`, and configuration helpers in `infrastructure/config`.
- `shared/` — Cross-cutting utilities and exceptions that remain independent of specific features and can be reused across layers.

## 4. Resources and Test Layout
- `src/main/resources/` — Configuration files, database migration scripts, and static assets. Place Flyway or Liquibase migrations under `src/main/resources/db/` as the persistence layer matures.
- `src/test/java/` — JUnit 5 test suites that mirror the production package tree (`application`, `domain`, `adapter`, `shared`). Maintain the `<ComponentName>Test` naming style and keep fixtures or builders close to the tests that use them.
- `src/test/resources/` — Sample syllabi, SQL fixtures, or JSON payloads that support deterministic tests.

## 5. Build and Quality Flow
- Run `mvn clean compile` for local validation when wiring new modules.
- Execute `mvn test` before committing to uphold the testing expectations.
- Treat `mvn clean verify` as the pre-push gate once integration adapters are in place.

## 6. Development Flow on the Current Skeleton
Follow the staged approach defined in `doc/timeline-proposal.md`:
1. Establish persistence (Leo) so repositories, migrations, and transaction ports are stable for downstream features.
2. Complete the syllabus ingestion adapters (Brody) to populate assessments and tasks that the remaining services depend on.
3. Build and harden task management flows (Rayan), ensuring adapters in `adapter/inbound/web` expose editing endpoints that other features can trust.
4. Deliver authentication and session control (Eric) so secure access wraps the task and grade endpoints before public exposure.
5. Implement grade calculation (Mike) using the persisted task and assessment data to serve actionable projections.
6. Finalize calendar export (Andy) once task data is reliable, using outbound adapters to write `.ics` files or push to Google Calendar.

Parallel to these milestones, update documentation in `doc/` when delivery-ready features ship, add targeted tests in the mirrored package under `src/test/java`, and keep non-source assets inside `src/main/resources` for consistent packaging.

Structure proposal authored by Andy Chen.
