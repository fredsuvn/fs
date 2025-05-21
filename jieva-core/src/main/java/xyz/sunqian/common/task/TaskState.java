package xyz.sunqian.common.task;

/**
 * Represents the state of a task.
 * <p>
 * There are two types of states: terminal and non-terminal, and they can be checked via {@link #isTerminal()}. The
 * state changes of a task are typically as follows:
 * <pre>
 *     {@link #WAITING} -> {@link #EXECUTING} -> ({@link #SUCCEEDED} | {@link #FAILED} | {@link #CANCELED_EXECUTING})
 *     or
 *     {@link #WAITING} -> {@link #CANCELED}
 * </pre>
 *
 * @author sunqian
 */
public enum TaskState {

    /**
     * Represents the task is waiting for executing.
     */
    WAITING(false),

    /**
     * Represents the task is executing.
     */
    EXECUTING(false),

    /**
     * Represents the task has been completed successfully.
     */
    SUCCEEDED(true),

    /**
     * Represents the task has been completed but failed, typically due to an exception thrown by the task.
     */
    FAILED(true),

    /**
     * Represents the task was canceled before the execution.
     */
    CANCELED(true),

    /**
     * Represents the task was canceled during the execution.
     */
    CANCELED_EXECUTING(true),
    ;

    private final boolean terminalFlag;

    TaskState(boolean isTerminal) {
        this.terminalFlag = isTerminal;
    }

    /**
     * Returns whether this state is a terminal state.
     *
     * @return whether this state is a terminal state
     */
    public boolean isTerminal() {
        return terminalFlag;
    }
}
