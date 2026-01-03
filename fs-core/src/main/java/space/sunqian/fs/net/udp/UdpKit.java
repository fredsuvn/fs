package space.sunqian.fs.net.udp;

import space.sunqian.annotation.Nonnull;

import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

/**
 * Utilities for UDP.
 *
 * @author sunqian
 */
public class UdpKit {

    static void channelRead(
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

    private UdpKit() {
    }
}
