package space.sunqian.common.net.udp;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;

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
