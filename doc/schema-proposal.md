# Schema Proposal — Syllabus Assistant
_Prepared on Nov 9, 2025 by Andy Chen_

_Modified on Nov 21, 2025 by Leo Wang to accommodate the new changes_
## Purpose
This document defines the relational schema that anchors persistence for the Syllabus Assistant. It aligns with the domain entities captured in `doc/project-blueprint.md` and the sequencing described in `doc/timeline-proposal.md`. All tables target SQLite for local development while keeping the design portable to other relational engines.

## Design Principles
- **Stable identifiers** — Every table uses externally generated `TEXT` primary keys (UUID-friendly) to keep services deterministic across imports.
- **Layer alignment** — Table names map directly to aggregates in `entity`, simplifying repository implementations and keeping `use_case` ports independent from data access concerns.
- **Cascade hygiene** — Foreign keys employ `ON DELETE CASCADE` or `SET NULL` so dependent records cannot orphan when upstream data is removed.
- **Enum safety** — `CHECK` constraints specify valid values for status and type columns to catch invalid writes early.

## Table Overview

[//]: # (![picture]&#40;./database-tables.png&#41;)
- **`users`** — Stores authentication and profile information, including hashed passwords and time zone preferences used for schedule rendering.
- **`courses`** — Represents a course owned by a user, capturing metadata such as term and instructor.
- **`syllabi`** — Tracks uploaded syllabus sources, parsed timestamps, and raw text snapshots for reprocessing or auditing.

[//]: # (- **`marking_schemes`** — Holds grading blueprints per course; multiple schemes allow alternative grading policies.)

[//]: # (- **`marking_scheme_components`** — Normalizes weight components &#40;assignments, exams, etc.&#41; for each scheme.)
- **`assessments`** — Individual assessments extracted from syllabi, optionally tied to a scheme component.

[//]: # (- **`tasks`** — &#40;Similar to assessments, this table will be added once everything is complete&#41; Actionable items surfaced to the user, linked to assessments when applicable.)
[//]: # (- **`grade_entries`** — Logged grades for assessments, enabling projections and historical tracking.)
- **`schedule_events`** — Events exported to calendars, tagged by source (`ASSESSMENT` or `TASK`) for traceability.

## Relationships & Indexes
- `courses.user_id → users.user_id` cascades deletes to child records.

[//]: # (- `assessments` reference both `courses` and optional `marking_scheme_components`.)

[//]: # (- `tasks` bridge users, courses, and assessments to support personal prioritization.)

[//]: # (- Secondary indexes accelerate common lookups &#40;e.g., tasks by user/status, assessments by course&#41;.)

## Proposed DDL
The following DDL is synchronized with the section in `README.md` and applied to `src/main/resources/db/syllabus_assistant.db` for local development.

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
