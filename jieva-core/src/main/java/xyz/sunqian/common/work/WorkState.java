package xyz.sunqian.common.work;

/**
 * Represents the state of a {@link Work}.
 * <p>
 * There are two types of states: terminal and non-terminal. The type of the state can be checked via
 * {@link #isTerminal()}. The state changes of a work are typically as follows:
 * <pre>
 *     {@link #WAITING} -> {@link #EXECUTING} -> ({@link #SUCCEEDED} | {@link #FAILED} | {@link #CANCELED_DURING})
 *     or
 *     {@link #WAITING} -> {@link #CANCELED}
 * </pre>
 *
 * @author sunqian
 */
public enum WorkState {

    /**
     * Represents the work is waiting for executing.
     */
    WAITING(false),

    /**
     * Represents the work is executing.
     */
    EXECUTING(false),

    /**
     * Represents the work has been completed successfully.
     */
    SUCCEEDED(true),

    /**
     * Represents the work has been completed but failed, typically due to an exception thrown by the work.
     */
    FAILED(true),

    /**
     * Represents the work was canceled before execution.
     */
    CANCELED(true),

    /**
     * Represents the work was canceled during execution .
     */
    CANCELED_DURING(true),
    ;

    private final boolean terminalFlag;

    WorkState(boolean isTerminal) {
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
