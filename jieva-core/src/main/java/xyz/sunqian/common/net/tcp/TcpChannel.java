package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.io.communicate.IOChannel;

import java.net.InetSocketAddress;

/**
 * {@link IOChannel} for TCP network, can get the remote and local address.
 *
 * @author sunqian
 */
public interface TcpChannel extends IOChannel {

    /**
     * Returns the remote address of this channel.
     *
     * @return the remote address of this channel
     */
    @Nonnull
    InetSocketAddress remoteAddress();

    /**
     * Returns the local address of this channel.
     *
     * @return the local address of this channel
     */
    @Nonnull
    InetSocketAddress localAddress();
}
