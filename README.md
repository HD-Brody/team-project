# Syllabus Assistant — Team M.A.R.B.L.E.

## Overview
Syllabus Assistant is a Java application (currently targeting Java 25 per the Maven compiler config) that ingests course syllabi, builds a consolidated task plan, projects grades, and exports prioritized schedules to calendar tools. The repository uses a simplified six-folder layout to keep responsibilities clear and independent.

## Core User Stories & Leads
- Upload syllabus PDFs and extract assessments — **Brody**
- View and edit tasks and due dates — **Rayan**
- Calculate grade targets using course weighting — **Mike**
- Export tasks to calendar formats — **Andy**
- Persist user/course/task data between sessions — **Leo**
- Support user login and account creation — **Eric**
- (Future) Recommend task prioritization — pending assignment

## Technology & Integrations
- Build tool: Maven (`pom.xml`) targeting Java 25
- Persistence: SQLite via JDBC adapters
- Document parsing: Apache PDFBox
- AI-assisted syllabus parsing: Google Gemini API
- Calendar export: iCal4j and Google Calendar API

## Current Status & Next Milestones
- Week 0 (Complete): Repository structure, planning documents, and timeline drafted.
- Week 1: Stand up persistence layer, connect syllabus ingestion stubs, define authentication primitives.
- Week 2: Integrate PDF/AI ingestion, harden persistence, implement task editing, and progress on grade calculation.
- Week 3: Finalize task endpoints, grade projections, and calendar export adapters; execute end-to-end QA.

Refer to `doc/timeline-proposal.md` for the detailed schedule and dependencies between workstreams.

## Repository Layout
- `src/main/java` — Application code organized into:
  - `app/` — Application bootstrap and configuration.
  - `data_access/` — Gateways and integrations (persistence, parsing, AI, calendar, config).
  - `entity/` — Core domain entities, value types, enums, and exceptions.
  - `interface_adapter/` — Controllers, presenters, and outbound adapters.
  - `use_case/` — Use case services, ports, repositories, and DTOs.
  - `view/` — UI entry points (e.g., CLI `view/cli/Main.java`; calendar export Swing demo `view/calendar/CalendarExportSwingDemo.java`).
- `src/main/resources` — Configuration, database migrations, and static assets.
- `src/test/java` & `src/test/resources` — Unit and integration tests with supporting fixtures.
- `doc/` — Project documentation, including the blueprint, timeline, and structure proposal (`doc/structure-proposal.md`).
- `target/` — Maven build output (not committed).

See `doc/structure-proposal.md` for a plain-language guide to these directories and how to extend them during development.

## Database Schema (SQLite)
The core tables capture users, courses, parsed syllabus data, assessments, tasks, grades, and calendar exports. Relationships cascade deletes so dependent records are cleaned automatically.

```sql
PRAGMA foreign_keys = ON;

CREATE TABLE users (
    user_id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    timezone TEXT,
    password_hash TEXT NOT NULL,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE courses (
    course_id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    code TEXT NOT NULL,
    name TEXT NOT NULL,
    term TEXT,
    meeting_info TEXT,
    instructor TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE syllabi (
    syllabus_id TEXT PRIMARY KEY,
    course_id TEXT NOT NULL,
    source_file_path TEXT,
    parsed_at TEXT,
    raw_text TEXT,
    version INTEGER DEFAULT 1,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

CREATE TABLE marking_schemes (
    scheme_id TEXT PRIMARY KEY,
    course_id TEXT NOT NULL,
    title TEXT,
    effective_from TEXT,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE (course_id, title)
);

CREATE TABLE marking_scheme_components (
    component_id TEXT PRIMARY KEY,
    scheme_id TEXT NOT NULL,
    name TEXT NOT NULL,
    type TEXT NOT NULL,
    weight REAL NOT NULL,
    component_count INTEGER,
    FOREIGN KEY (scheme_id) REFERENCES marking_schemes(scheme_id) ON DELETE CASCADE,
    CHECK (weight >= 0 AND weight <= 1),
    CHECK (type IN ('TEST','ASSIGNMENT','EXAM','QUIZ','PROJECT','OTHER'))
);

CREATE TABLE assessments (
    assessment_id TEXT PRIMARY KEY,
    course_id TEXT NOT NULL,
    component_id TEXT,
    title TEXT NOT NULL,
    type TEXT NOT NULL,
    starts_at TEXT,
    ends_at TEXT,
    duration_minutes INTEGER,
    weight REAL,
    location TEXT,
    notes TEXT,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (component_id) REFERENCES marking_scheme_components(component_id) ON DELETE SET NULL,
    CHECK (weight IS NULL OR (weight >= 0 AND weight <= 1)),
    CHECK (type IN ('TEST','ASSIGNMENT','EXAM','QUIZ','PROJECT','OTHER'))
);

CREATE TABLE tasks (
    task_id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    course_id TEXT NOT NULL,
    assessment_id TEXT,
    title TEXT NOT NULL,
    due_at TEXT,
    estimated_effort_mins INTEGER,
    priority INTEGER,
    status TEXT NOT NULL DEFAULT 'TODO',
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (assessment_id) REFERENCES assessments(assessment_id) ON DELETE SET NULL,
    CHECK (status IN ('TODO','IN_PROGRESS','DONE','CANCELLED')),
    CHECK (priority IS NULL OR priority BETWEEN 1 AND 5)
);

CREATE TABLE grade_entries (
    grade_entry_id TEXT PRIMARY KEY,
    assessment_id TEXT NOT NULL,
    points_earned REAL,
    points_possible REAL,
    percent REAL,
    graded_at TEXT,
    feedback TEXT,
    FOREIGN KEY (assessment_id) REFERENCES assessments(assessment_id) ON DELETE CASCADE,
    CHECK (percent IS NULL OR (percent >= 0 AND percent <= 100))
);

CREATE TABLE schedule_events (
    event_id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    title TEXT NOT NULL,
    starts_at TEXT NOT NULL,
    ends_at TEXT,
    location TEXT,
    notes TEXT,
    source_kind TEXT NOT NULL,
    source_id TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CHECK (source_kind IN ('ASSESSMENT','TASK'))
);

CREATE INDEX idx_courses_user ON courses(user_id);
CREATE INDEX idx_syllabi_course ON syllabi(course_id);
CREATE INDEX idx_marking_components_scheme ON marking_scheme_components(scheme_id);
CREATE INDEX idx_assessments_course ON assessments(course_id);
CREATE INDEX idx_tasks_user_status ON tasks(user_id, status);
CREATE INDEX idx_grade_entries_assessment ON grade_entries(assessment_id);
CREATE INDEX idx_schedule_events_user ON schedule_events(user_id);
```

## Build & Test
```bash
mvn clean compile   # validate sources
mvn test            # run the JUnit 5 test suite
mvn clean verify    # full build plus integration checks before pushing
```

## Additional Documentation
- `doc/project-blueprint.md` — Product vision, user stories, and proposed domain entities.
- `doc/timeline-proposal.md` — Development phases, ownership, and QA expectations.
- `doc/TeamContract.md` — Collaboration norms and responsibilities.
- `doc/schema-proposal.md` — Database rationale, table ownership, and migration guidance.
