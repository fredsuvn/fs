package xyz.sunqian.common.net.udp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.net.NetException;

import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

final class UdpSenderImpl implements UdpSender {

    private final @Nonnull DatagramChannel channel;

    UdpSenderImpl(boolean broadcast) throws Exception {
        this.channel = DatagramChannel.open();
        channel.configureBlocking(true);
        if (broadcast) {
            channel.setOption(StandardSocketOptions.SO_BROADCAST, true);
        }
    }

    @Override
    public void sendBuffer(@Nonnull ByteBuffer data, @Nonnull SocketAddress address) throws NetException {
        Jie.uncheck(() -> {
            channel.send(data, address);
        }, NetException::new);
    }

    @Override
    public @Nonnull DatagramChannel datagramChannel() {
        return channel;
    }
}
