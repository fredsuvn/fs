package xyz.sunqian.common.io.communicate;

/**
 * Represents a context of an IO channel, can be used to obtain information from the remote endpoint of the channel and
 * send data to it.
 *
 * @param <A> the type of the channel address
 * @param <R> the type of the channel reader
 * @param <W> the type of the channel writer
 * @author sunqian
 */
public interface IOChannelContext<A, R extends IOChannelReader, W extends IOChannelWriter> {

    /**
     * Returns the remote address of the channel.
     *
     * @return the remote address of the channel
     * @throws Exception for any error
     */
    A remoteAddress() throws Exception;

    /**
     * Returns the local address of the channel.
     *
     * @return the local address of the channel
     * @throws Exception for any error
     */
    A localAddress() throws Exception;

    /**
     * Returns a reader that can be used to read data from the remote endpoint of this channel.
     *
     * @return a reader that can be used to read data from the remote endpoint of this channel.
     */
    R reader();

    /**
     * Returns a writer that can be used to write data to the remote endpoint of this channel.
     *
     * @return a writer that can be used to write data to the remote endpoint of this channel.
     */
    W writer();

    /**
     * Close this channel.
     *
     * @throws Exception for any error
     */
    void close() throws Exception;

    /**
     * Returns whether this channel is open.
     *
     * @return whether this channel is open
     */
    boolean isOpen();
}
