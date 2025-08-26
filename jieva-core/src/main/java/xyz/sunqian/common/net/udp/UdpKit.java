package xyz.sunqian.common.net.udp;

import xyz.sunqian.annotations.Nonnull;

import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

/**
 * Utilities for UDP.
 *
 * @author sunqian
 */
public class UdpKit {

    /**
     * Invokes {@link UdpServerHandler#channelRead(DatagramChannel, byte[], SocketAddress)}, and all {@link Throwable}
     * thrown by it will be caught and passed to {@link UdpServerHandler#exceptionCaught(DatagramChannel, Throwable)}.
     *
     * @param handler the handler of which {@link UdpServerHandler#channelRead(DatagramChannel, byte[], SocketAddress)}
     *                is invoked
     * @param channel the channel to be handled
     */
    public static void channelRead(
        @Nonnull UdpServerHandler handler,
        @Nonnull DatagramChannel channel,
        byte @Nonnull [] data,
        @Nonnull SocketAddress address
    ) {
        try {
            handler.channelRead(channel, data, address);
        } catch (Throwable e) {
            handler.exceptionCaught(channel, e);
        }
    }
}
