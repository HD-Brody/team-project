package interface_adapter.inbound.web.exception;

/**
 * Exception thrown when a requested task cannot be found.
 * Interface adapter layer exception for clearer error handling.
 */
public class TaskNotFoundException extends RuntimeException {
    private final String taskId;

    public TaskNotFoundException(String message) {
        super(message);
        this.taskId = null;
    }

    public TaskNotFoundException(String message, String taskId) {
        super(message);
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}

