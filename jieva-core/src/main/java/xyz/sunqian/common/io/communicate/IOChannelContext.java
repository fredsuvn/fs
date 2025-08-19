package xyz.sunqian.common.io.communicate;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Represents a context of an IO channel, can be used to obtain information from the remote endpoint of the channel and
 * send data to it.
 *
 * @param <A> the type of the channel address
 * @author sunqian
 */
public interface IOChannelContext<A> {

    /**
     * Returns the remote address of the channel.
     *
     * @return the remote address of the channel
     * @throws Exception for any error
     */
    A getRemoteAddress() throws Exception;

    /**
     * Returns the local address of the channel.
     *
     * @return the local address of the channel
     * @throws Exception for any error
     */
    A getLocalAddress() throws Exception;

    /**
     * Write the data to the channel.
     *
     * @param buffer the data to be written
     * @throws Exception for any error
     */
    void write(ByteBuffer buffer) throws Exception;

    /**
     * Returns a writable byte channel that can be used to write data to the channel.
     *
     * @return a writable byte channel that can be used to write data to the channel
     */
    WritableByteChannel asWritableByteChannel();

    /**
     * Close this channel.
     *
     * @throws Exception for any error
     */
    void close() throws Exception;
}
