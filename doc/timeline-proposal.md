# Development Timeline Proposal

_Prepared by Andy Chen on Nov 5, 2025 for team review; kickoff planned for the week of Nov 10, 2025._
_This proposal will be submitted via pull request for team approval. Adjustments can be made once availability or external dependencies change._
# Overview
## Prioritized Workstreams (by criticality)
1. **Data Persistence Platform — Leo**  
   Scope: `application/service/CoursePersistenceService.java`, `application/port/incoming/CoursePersistenceUseCase.java`, `application/dto/CourseSnapshot.java`, every contract in `domain/repository/`, plus adapters under `adapter/outbound/persistence/` and storage wiring in `infrastructure/persistence/sqlite/` with migrations in `src/main/resources/db/`.  
   Dependency: Unblocks all other use cases by providing durable storage and transactional boundaries.
2. **Upload Syllabus Pipeline — Brody**  
   Scope: `application/service/SyllabusUploadService.java`, DTOs in `application/dto/` (`UploadSyllabusCommand`, `AssessmentDraft`, `WeightComponentDraft`, `SyllabusParseResult`), ingestion ports in `application/port/outgoing/`, and adapters under `adapter/outbound/parser/`, `adapter/outbound/ai/`, `infrastructure/parser/pdf/`, and `infrastructure/ai/gemini/`.  
   Dependency: Requires Leo’s repositories; feeds data to Rayan, Mike, and Andy.
3. **Task Management (View/Edit) — Rayan**  
   Scope: `application/service/TaskEditingService.java`, `application/port/incoming/TaskEditingUseCase.java`, `application/dto/TaskUpdateCommand.java`, domain models `domain/model/Task.java` & `TaskStatus.java`, and inbound controllers in `adapter/inbound/web/`.  
   Dependency: Consumes Brody’s ingested tasks persisted by Leo.
4. **Authentication & Session Control — Eric**  
   Scope: `application/service/AuthenticationService.java`, `application/port/incoming/AuthenticationUseCase.java`, DTOs (`UserCredentials`, `UserRegistrationCommand`, `AuthenticationResult`), domain `domain/model/User.java`, repository `domain/repository/UserRepository.java`, and security adapters via `PasswordHashingPort`, `TokenProviderPort`, and configuration in `infrastructure/config/`.  
   Dependency: Relies on Leo’s persistence; required before exposing protected task/grade endpoints.
5. **Grade Calculation — Mike**  
   Scope: `application/service/GradeCalculationService.java`, `application/port/incoming/GradeCalculationUseCase.java`, DTOs (`GradeCalculationRequest`, `GradeCalculationResponse`), and related repositories (`AssessmentRepository`, `MarkingSchemeRepository`, `GradeEntryRepository`).  
   Dependency: Needs Brody’s assessment data and Leo’s persistence; downstream of Rayan for task edits affecting grades.
6. **Calendar Export — Andy**  
   Scope: `application/service/CalendarExportService.java`, `application/port/incoming/CalendarExportUseCase.java`, DTOs (`CalendarExportRequest`, `CalendarExportResult`), domain `domain/model/ScheduleEvent.java`, `Task.java`, outgoing `CalendarPublisherPort`, and adapters under `adapter/outbound/calendar/` plus integrations in `infrastructure/calendar/google/`.  
   Dependency: Requires stable task schedules (Rayan) and persisted events/tasks (Leo, Brody).

## Dependency Information
- Leo’s persistence work is the foundation; every other stream depends on the repositories, migrations, and transaction support landing first.
- Brody’s ingestion populates the assessments/tasks that unlock Rayan, Mike, and Andy’s deliverables.
- Rayan’s task editing must stabilize before Mike and Andy can trust task data for grade targets and calendar exports.
- Eric’s authentication gates user-facing adapters; his APIs should wrap Rayan and Mike’s endpoints once those are ready.
- Andy’s calendar export relies on Brody’s ingested assessments, Leo’s persisted schedule data, and Rayan’s finalized task editing flows before adapters push events to iCal/Google.

## QA & Testing Rules 
- Unit tests live beside the layer they exercise (`src/test/java/domain`, `application`, `adapter`) and follow the naming pattern `<ComponentName>Test`.  
- Minimum expectations: ≥80% line coverage for new application/domain code, zero ignored tests without justification, and deterministic tests (no real network calls).  
- Before merge, the feature lead runs `mvn test` plus any targeted integration tests in `adapter/outbound` or `infrastructure`, capturing command output in the PR.  
- Manual QA checklists accompany any user-facing change, outlining steps, expected results, and screenshots/logs when relevant.  
- Documentation updates (`doc/`) are authored concurrently by the responsible lead when their feature reaches review-ready status.

## Timeline (Week 0 – Week 3)
**Week 0 — Nov 3 to Nov 7**  
- [x] Status: Completed (Nov 5, 2025)  
  - Andy: Orchestrate repository structure per Clean Architecture guidelines, organize documentation, and draft this proposal. 
- [ ] Status: Pending
  - Team: Review and approve timeline proposal; identify any blockers or resource constraints.

**Week 1 — Nov 10 to Nov 14**  
- [ ] Status: Pending  
  - Leo: Stand up SQLite schema, implement `TransactionalPersistencePort`, and deliver repository adapters in `adapter/outbound/persistence/` with baseline migrations.  
- [ ] Status: Pending  
  - Brody: Wire `SyllabusUploadService` through Leo’s repositories; create contract tests and stub adapters in `adapter/outbound/parser/`, `adapter/outbound/ai/`, and `infrastructure/parser/pdf/`.  
- [ ] Status: Pending  
  - Eric: Define hashing/token adapters, stub `AuthenticationService` flows, and ensure user persistence primitives exist.  
- [ ] Status: Pending  
  - Team: Lock DTO contracts and validation rules; each lead drafts outline docs for their use case in `doc/`. 

**Week 2 — Nov 17 to Nov 21**  
- [ ] Status: Pending  
  - Brody: Integrate PDFBox and Gemini adapters for end-to-end ingestion, persisting assessments/tasks with retry/error reporting.  
- [ ] Status: Pending  
  - Leo: Harden transaction handling, add migration automation (`src/main/resources/db/migrations`), and supply seed data scripts for QA.  
- [ ] Status: Pending  
  - Rayan: Implement `TaskEditingService` load/update logic, publish controller stubs in `adapter/inbound/web/`, and add service-layer tests.  
- [ ] Status: Pending  
  - Eric: Complete register/login flows with hashing + token issuance, write negative-path tests, and document auth endpoints.  
- [ ] Status: Pending  
  - Mike: Begin grade calculation logic using ingested assessments; deliver initial unit tests covering weighting math. 

**Week 3 — Nov 24 to Nov 28**  
- [ ] Status: Pending  
  - Rayan: Finalize task view/edit endpoints, align DTOs with UI, and ensure updates cascade to dependent services.  
- [ ] Status: Pending  
  - Mike: Finish grade projections, expose application endpoints, and validate integration with updated tasks and marking schemes.  
- [ ] Status: Pending  
  - Andy: Build calendar export aggregation, implement ICS/Google adapters, and cover export logic with integration fixtures.  
- [ ] Status: Pending  
  - Team: Execute end-to-end QA (upload → edit → grade → export) following shared checklists; resolve defects immediately and finalize per-use-case documentation. 
