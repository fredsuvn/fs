package xyz.sunqian.common.net;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.io.communicate.IOChannelContext;

import java.net.InetSocketAddress;

/**
 * {@link IOChannelContext} for network, can be used to obtain information of the channel and receive/send data between
 * this point and the remote endpoint.
 *
 * @author sunqian
 */
public interface NetChannelContext extends IOChannelContext<InetSocketAddress> {

    /**
     * Returns the type of the channel.
     *
     * @return the type of the channel
     */
    @Nonnull
    NetChannelType channelType();

    @Override
    @Nonnull
    InetSocketAddress remoteAddress() throws NetException;

    @Override
    @Nonnull
    InetSocketAddress localAddress() throws NetException;

    @Override
    void close() throws NetException;

    @Override
    boolean isOpen();
}
