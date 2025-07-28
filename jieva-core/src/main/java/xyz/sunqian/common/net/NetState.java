package xyz.sunqian.common.net;

/**
 * The current state of a {@link NetServer} or {@link NetClient}.
 *
 * @author sunqian
 */
public enum NetState {

    /**
     * Represents a server or client that has been created but not yet started.
     */
    CREATED,

    /**
     * Represents a server or client that is starting.
     */
    STARTUP,

    /**
     * Represents a server or client that is running.
     */
    RUNNING,

    /**
     * Represents a server or client that has failed to start.
     */
    START_FAILED,

    /**
     * Represents a server or client that is closing.
     */
    CLOSING,

    /**
     * Represents a server or client that has been closed.
     */
    CLOSED,
    ;
}
