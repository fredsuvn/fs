package xyz.sunqian.common.net;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.util.List;

/**
 * This interface represents a network server.
 * <p>
 * A network server is typically has a main thread, which is responsible for accepting new client, and a group of worker
 * threads, which is responsible for handling connected client.
 *
 * @param <A> the type of the address in the network
 * @author sunqian
 */
public interface NetServer<A> {

    /**
     * Starts this server. This method returns immediately, using {@link #await()} to wait for the server to terminate.
     *
     * @throws NetException if any error occurs
     */
    void start() throws NetException;

    /**
     * Closes this server. This method interrupts all thread of this server, and waits for all threads to terminate.
     *
     * @throws NetException if any error occurs
     */
    void close() throws NetException;

    /**
     * Blocks current thread until all threads of this server terminates.
     *
     * @throws NetException if any error occurs
     */
    void await() throws NetException;

    /**
     * Returns the address this server is bound to, may be {@code null} if the server is not started.
     *
     * @return the address this server is bound to, may be {@code null} if the server is not started
     */
    @Nullable
    A localAddress();

    /**
     * Returns all current workers of this server.
     *
     * @return all current workers of this server
     */
    List<Worker> workers();

    /**
     * Returns whether this server is started.
     *
     * @return whether this server is started
     */
    boolean isStarted();

    /**
     * Returns whether this server is closed.
     *
     * @return whether this server is closed
     */
    boolean isClosed();

    /**
     * Represents the worker of the server. A worker typically is responsible for handling connected clients, and
     * typically contains a worker thread.
     *
     * @author sunqian
     */
    interface Worker {

        /**
         * Returns the number of connected clients this worker handles.
         *
         * @return the number of connected clients this worker handles
         */
        int clientCount();

        /**
         * Returns the thread this worker runs on.
         *
         * @return the thread this worker runs on
         */
        @Nonnull
        Thread thread();
    }
}
