package xyz.sunqian.common.net;

/**
 * This interface represents a network client, as opposed to the server.
 *
 * @author sunqian
 */
public interface NetClient {

    /**
     * Disconnect the connection between the client and server.
     *
     * @throws NetException if any error occurs
     */
    void disconnect() throws NetException;
}
