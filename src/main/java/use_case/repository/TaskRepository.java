package use_case.repository;

import entity.Task;
import java.util.List;
import java.util.Optional;

/**
 * Persistence boundary for planning tasks.
 * 
 * <h3>Implementation Notes for Leo:</h3>
 * <ul>
 *   <li>Store Instant fields as TEXT in ISO-8601 format (e.g., "2025-11-20T23:59:00Z")</li>
 *   <li>Store TaskStatus enum as TEXT (e.g., "TODO", "IN_PROGRESS", "DONE", "CANCELLED")</li>
 *   <li>Use "INSERT OR REPLACE" for save() to support both insert and update</li>
 *   <li>Return empty Optional (not null) when task not found</li>
 * </ul>
 * 
 * <h3>Required Database Table:</h3>
 * <pre>
 * CREATE TABLE tasks (
 *     task_id TEXT PRIMARY KEY,
 *     user_id TEXT NOT NULL,
 *     course_id TEXT NOT NULL,
 *     assessment_id TEXT,
 *     title TEXT NOT NULL,
 *     due_at TEXT,
 *     estimated_effort_mins INTEGER,
 *     priority INTEGER,
 *     status TEXT NOT NULL DEFAULT 'TODO',
 *     notes TEXT,
 *     FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
 *     FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
 *     FOREIGN KEY (assessment_id) REFERENCES assessments(assessment_id) ON DELETE SET NULL,
 *     CHECK (status IN ('TODO','IN_PROGRESS','DONE','CANCELLED')),
 *     CHECK (priority IS NULL OR priority BETWEEN 1 AND 5)
 * );
 * 
 * CREATE INDEX idx_tasks_user_status ON tasks(user_id, status);
 * </pre>
 */
public interface TaskRepository {
    
    /**
     * Retrieves a single task by its unique identifier.
     * 
     * <p><b>SQL Query:</b>
     * <pre>
     * SELECT task_id, user_id, course_id, assessment_id, title, due_at,
     *        estimated_effort_mins, priority, status, notes
     * FROM tasks
     * WHERE task_id = ?
     * </pre>
     *
     * @param taskId the unique task identifier (UUID format)
     * @return Optional containing the task if found, empty otherwise
     * @throws NullPointerException if taskId is null
     */
    Optional<Task> findById(String taskId);

    /**
     * Retrieves all tasks for a specific user.
     * 
     * <p><b>SQL Query:</b>
     * <pre>
     * SELECT task_id, user_id, course_id, assessment_id, title, due_at,
     *        estimated_effort_mins, priority, status, notes
     * FROM tasks
     * WHERE user_id = ?
     * ORDER BY due_at ASC NULLS LAST, priority DESC NULLS LAST
     * </pre>
     * 
     * <p><b>Performance:</b> Uses index idx_tasks_user_status
     *
     * @param userId the user's unique identifier
     * @return list of all tasks for the user (may be empty, never null)
     * @throws NullPointerException if userId is null
     */
    List<Task> findByUserId(String userId);

    /**
     * Persists a task (insert if new, update if exists).
     * 
     * <p><b>Implementation:</b> Use INSERT OR REPLACE (SQLite upsert).
     * 
     * <p><b>Data Conversions Required:</b>
     * <ul>
     *   <li>Instant → TEXT: {@code instant.toString()} produces "2025-11-20T23:59:00Z"</li>
     *   <li>TaskStatus → TEXT: {@code status.name()} produces "TODO", "IN_PROGRESS", etc.</li>
     *   <li>Integer null → SQL NULL</li>
     * </ul>
     * 
     * <p><b>SQL Query:</b>
     * <pre>
     * INSERT OR REPLACE INTO tasks (
     *     task_id, user_id, course_id, assessment_id, title, due_at,
     *     estimated_effort_mins, priority, status, notes
     * ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     * </pre>
     *
     * @param task the task to save (must not be null)
     * @throws NullPointerException if task is null
     * @throws IllegalArgumentException if task.getTaskId() is null
     */
    void save(Task task);

    /**
     * Deletes a task by its unique identifier.
     * 
     * <p><b>Behavior:</b> Idempotent - does not throw exception if task doesn't exist.
     * Related schedule_events will be cascade-deleted automatically via DB FK.
     * 
     * <p><b>SQL Query:</b>
     * <pre>
     * DELETE FROM tasks WHERE task_id = ?
     * </pre>
     *
     * @param taskId the unique task identifier
     * @throws NullPointerException if taskId is null
     */
    void deleteById(String taskId);
}
