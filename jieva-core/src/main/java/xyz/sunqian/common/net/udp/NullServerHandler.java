package xyz.sunqian.common.net.udp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

enum NullServerHandler implements UdpServerHandler {

    INST;

    @Override
    public void channelRead(
        @Nonnull DatagramChannel channel, byte @Nonnull [] data, @Nonnull SocketAddress address
    ) {
    }

    @Override
    public void exceptionCaught(@Nullable DatagramChannel channel, @Nonnull Throwable cause) {
    }
}
