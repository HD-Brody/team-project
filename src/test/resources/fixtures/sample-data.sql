PRAGMA foreign_keys = ON;

DELETE FROM schedule_events;
DELETE FROM grade_entries;
DELETE FROM tasks;
DELETE FROM assessments;
DELETE FROM marking_scheme_components;
DELETE FROM marking_schemes;
DELETE FROM syllabi;
DELETE FROM courses;
DELETE FROM users;

INSERT INTO users (user_id, name, email, timezone, password_hash, created_at, updated_at)
VALUES ('user-1', 'Test User', 'test.user@example.com', 'America/Toronto', 'HASHED_PASSWORD', '2025-11-10T10:00:00Z', '2025-11-10T10:00:00Z');

INSERT INTO courses (course_id, user_id, code, name, term, meeting_info, instructor)
VALUES ('course-1', 'user-1', 'CSC207', 'Software Design', 'Winter 2026', 'Mon/Wed 10:00-11:00', 'Prof. Ada Lovelace');

INSERT INTO syllabi (syllabus_id, course_id, source_file_path, parsed_at, raw_text, version)
VALUES ('syllabus-1', 'course-1', '/uploads/csc207.pdf', '2025-11-10T11:30:00Z', 'Sample syllabus text', 1);

INSERT INTO marking_schemes (scheme_id, course_id, title, effective_from)
VALUES ('scheme-1', 'course-1', 'Default Scheme', '2025-09-01T00:00:00Z');

INSERT INTO marking_scheme_components (component_id, scheme_id, name, type, weight, component_count)
VALUES
  ('component-1', 'scheme-1', 'Assignments', 'ASSIGNMENT', 0.40, 4),
  ('component-2', 'scheme-1', 'Midterm Exam', 'EXAM', 0.25, 1),
  ('component-3', 'scheme-1', 'Final Project', 'PROJECT', 0.35, 1);

INSERT INTO assessments (assessment_id, course_id, component_id, title, type, starts_at, ends_at, duration_minutes, weight, location, notes)
VALUES
  ('assessment-1', 'course-1', 'component-1', 'Assignment 1', 'ASSIGNMENT', '2025-11-12T09:00:00Z', '2025-11-19T23:59:00Z', NULL, 0.10, NULL, 'Submit via portal'),
  ('assessment-2', 'course-1', 'component-2', 'Midterm Exam', 'EXAM', '2025-11-25T14:00:00Z', '2025-11-25T16:00:00Z', 120, 0.25, 'Room BA123', NULL);

INSERT INTO tasks (task_id, user_id, course_id, assessment_id, title, due_at, estimated_effort_mins, priority, status, notes)
VALUES
  ('task-1', 'user-1', 'course-1', 'assessment-1', 'Draft assignment outline', '2025-11-14T23:59:00Z', 120, 3, 'IN_PROGRESS', 'Focus on problem 2 first'),
  ('task-2', 'user-1', 'course-1', 'assessment-1', 'Finish assignment write-up', '2025-11-19T18:00:00Z', 180, 4, 'TODO', NULL),
  ('task-3', 'user-1', 'course-1', 'assessment-2', 'Midterm study session', '2025-11-23T21:00:00Z', 240, 5, 'TODO', 'Review lectures 1-6');

INSERT INTO grade_entries (grade_entry_id, assessment_id, points_earned, points_possible, percent, graded_at, feedback)
VALUES
  ('grade-1', 'assessment-1', 18.0, 20.0, 90.0, '2025-11-26T12:00:00Z', 'Great job!');

INSERT INTO schedule_events (event_id, user_id, title, starts_at, ends_at, location, notes, source_kind, source_id)
VALUES
  ('event-1', 'user-1', 'Assignment 1 Due', '2025-11-19T18:00:00Z', '2025-11-19T19:00:00Z', NULL, 'Auto-created from assessment', 'ASSESSMENT', 'assessment-1'),
  ('event-2', 'user-1', 'Midterm Exam', '2025-11-25T14:00:00Z', '2025-11-25T16:00:00Z', 'Room BA123', NULL, 'ASSESSMENT', 'assessment-2'),
  ('event-3', 'user-1', 'Study Session', '2025-11-23T19:00:00Z', '2025-11-23T21:00:00Z', 'Robarts Library', 'From task planning', 'TASK', 'task-3');
