package xyz.sunqian.common.net.udp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

final class NullServerHandler implements UdpServerHandler {

    static final @Nonnull NullServerHandler SINGLETON = new NullServerHandler();

    @Override
    public void channelRead(
        @Nonnull DatagramChannel channel, byte @Nonnull [] data, @Nonnull SocketAddress address
    ) {
    }

    @Override
    public void exceptionCaught(@Nullable DatagramChannel channel, @Nonnull Throwable cause) {
    }
}
