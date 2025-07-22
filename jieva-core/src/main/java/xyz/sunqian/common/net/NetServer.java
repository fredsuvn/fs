package xyz.sunqian.common.net;

/**
 * This interface represents a network server.
 *
 * @author sunqian
 */
public interface NetServer {

    /**
     * Starts this server, and returns immediately after startup. This method will not block the current thread,
     * {@link #await()} will.
     *
     * @throws NetException if any error occurs
     */
    void start() throws NetException;

    /**
     * Closes this server, releases all related resources.
     *
     * @throws NetException if any error occurs
     */
    void close() throws NetException;

    /**
     * Blocks current thread until this server is closed.
     *
     * @throws NetException if any error occurs
     */
    void await() throws NetException;
}
