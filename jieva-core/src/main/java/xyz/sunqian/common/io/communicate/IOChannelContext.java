package xyz.sunqian.common.io.communicate;

import xyz.sunqian.common.io.BufferKit;

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
    A remoteAddress() throws Exception;

    /**
     * Returns the local address of the channel.
     *
     * @return the local address of the channel
     * @throws Exception for any error
     */
    A localAddress() throws Exception;

    /**
     * Write the data to the remote endpoint of this channel.
     *
     * @param buffer the data to be written
     * @throws Exception for any error
     */
    default void write(ByteBuffer buffer) throws Exception {
        BufferKit.readTo(buffer, writer());
    }

    /**
     * Returns a {@link WritableByteChannel} that can be used to write data to the remote endpoint of this channel.
     *
     * @return a {@link WritableByteChannel} that can be used to write data to the remote endpoint of this channel.
     */
    WritableByteChannel writer();

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
