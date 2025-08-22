package xyz.sunqian.common.net;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.io.communicate.IOChannelContext;

import java.net.InetSocketAddress;

/**
 * Represents a context of a network channel including TCP and UDP, can be used to obtain information from the remote
 * endpoint of the channel and send data to it.
 *
 * @author sunqian
 */
public interface NetChannelContext extends IOChannelContext<InetSocketAddress, NetChannelReader, NetChannelWriter> {

    /**
     * Returns the type of the channel.
     *
     * @return the type of the channel
     */
    @Nonnull
    NetChannelType channelType();

    @Override
    @Nonnull
    InetSocketAddress remoteAddress() throws Exception;

    @Override
    @Nonnull
    InetSocketAddress localAddress() throws Exception;

    @Override
    @Nonnull
    NetChannelReader reader();

    @Override
    @Nonnull
    NetChannelWriter writer();

    @Override
    void close() throws Exception;

    @Override
    boolean isOpen();
}
