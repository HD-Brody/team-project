# Syllabus Assistant — Team M.A.R.B.L.E.

## Overview
Syllabus Assistant is a Java 11 application that ingests course syllabi, builds a consolidated task plan, projects grades, and exports prioritized schedules to calendar tools. The repository uses a simplified six-folder layout to keep responsibilities clear and independent.

## Core User Stories & Leads
- Upload syllabus PDFs and extract assessments — **Brody**
- View and edit tasks and due dates — **Rayan**
- Calculate grade targets using course weighting — **Mike**
- Export tasks to calendar formats — **Andy**
- Persist user/course/task data between sessions — **Leo**
- Support user login and account creation — **Eric**
- (Future) Recommend task prioritization — pending assignment

## Technology & Integrations
- Build tool: Maven (`pom.xml`) targeting Java 11
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
  - `view/` — UI entry points (e.g., CLI `view/cli/Main.java`).
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
    name TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    timezone TEXT,
    password_hash TEXT NOT NULL
);

CREATE TABLE courses (
    course_id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    code TEXT NOT NULL,
    name TEXT NOT NULL,
    term TEXT,
    instructor TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE syllabi (
    syllabus_id TEXT PRIMARY KEY,
    course_id TEXT NOT NULL,
    source_file_path TEXT,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

CREATE TABLE assessments (
    assessment_id TEXT PRIMARY KEY,
    course_id TEXT NOT NULL,
    title TEXT NOT NULL,
    type TEXT NOT NULL,
    grade REAL DEFAULT -1,
    starts_at TEXT,
    ends_at TEXT,
    duration_minutes INTEGER,
    weight REAL,
    location TEXT,
    notes TEXT,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    CHECK (weight IS NULL OR (weight >= 0 AND weight <= 1)),
    CHECK (type IN ('TEST','ASSIGNMENT','EXAM','QUIZ','PROJECT','OTHER'))
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
);
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
