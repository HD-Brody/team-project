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
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);