package space.sunqian.common.net.udp;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.Fs;
import space.sunqian.common.net.NetException;
import space.sunqian.common.net.NetKit;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

final class UdpSenderBack {

    static @Nonnull UdpSender newSender(boolean broadcast) throws Exception {
        return broadcast ? new BroadcastUdpSender() : new CommonUdpSender();
    }

    private static class AbsUdpSender implements UdpSender {

        protected final @Nonnull DatagramChannel channel;

        private AbsUdpSender() throws Exception {
            this.channel = DatagramChannel.open();
            channel.configureBlocking(true);
        }

        @Override
        public void sendData(@Nonnull ByteBuffer data, @Nonnull SocketAddress address) throws NetException {
            Fs.uncheck(() -> {
                channel.send(data, address);
            }, NetException::new);
        }

        @Override
        public @Nonnull InetAddress broadcastAddress() throws NetException {
            throw new NetException("This sender does not support broadcast.");
        }

        @Override
        public @Nonnull InetAddress refreshBroadcastAddress() throws NetException {
            throw new NetException("This sender does not support broadcast.");
        }

        @Override
        public void close() throws NetException {
            Fs.uncheck(channel::close, NetException::new);
        }

        @Override
        public @Nonnull DatagramChannel channel() {
            return channel;
        }
    }

    private static final class CommonUdpSender extends AbsUdpSender {
        private CommonUdpSender() throws Exception {
        }
    }

    private static final class BroadcastUdpSender extends AbsUdpSender {

        private @Nonnull InetAddress broadcastIp;

        private BroadcastUdpSender() throws Exception {
            super();
            channel.setOption(StandardSocketOptions.SO_BROADCAST, true);
            this.broadcastIp = NetKit.getBroadcastAddress();
        }

        @Override
        public @Nonnull InetAddress broadcastAddress() throws NetException {
            return broadcastIp;
        }

        @Override
        public @Nonnull InetAddress refreshBroadcastAddress() throws NetException {
            broadcastIp = NetKit.getBroadcastAddress();
            return broadcastIp;
        }
    }

    private UdpSenderBack() {
    }
}
