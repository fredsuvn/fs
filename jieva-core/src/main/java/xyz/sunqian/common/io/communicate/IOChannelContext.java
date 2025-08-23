package xyz.sunqian.common.io.communicate;

import xyz.sunqian.common.io.IORuntimeException;

/**
 * Context of an IO channel, can be used to obtain information of the channel and receive/send data between this point
 * and the remote endpoint.
 *
 * @param <A> the type of the channel address
 * @author sunqian
 */
public interface IOChannelContext<A> {

    /**
     * Returns the remote address of the channel.
     *
     * @return the remote address of the channel
     * @throws IORuntimeException for any error
     */
    A remoteAddress() throws IORuntimeException;

    /**
     * Returns the local address of the channel.
     *
     * @return the local address of the channel
     * @throws IORuntimeException for any error
     */
    A localAddress() throws IORuntimeException;

    /**
     * Returns the io channel between this point and the remote endpoint.
     *
     * @return the io channel between this point and the remote endpoint
     */
    IOChannel ioChannel();

    /**
     * Close this channel.
     *
     * @throws IORuntimeException for any error
     */
    void close() throws IORuntimeException;

    /**
     * Returns whether this channel is open.
     *
     * @return whether this channel is open
     */
    boolean isOpen();
}
