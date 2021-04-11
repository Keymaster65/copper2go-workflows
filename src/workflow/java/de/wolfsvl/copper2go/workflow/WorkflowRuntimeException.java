package de.wolfsvl.copper2go.workflow;

public class WorkflowRuntimeException extends RuntimeException {
    public WorkflowRuntimeException(final String message) {
        super(message);
    }
    public WorkflowRuntimeException(final String message, final Exception cause) {
        super(message, cause);
    }
}
