package xyz.sunqian.common.net;

/**
 * This interface represents a network server.
 *
 * @author sunqian
 */
public interface NetServer {

    /**
     * Starts this server.
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
}
