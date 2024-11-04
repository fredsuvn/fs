package xyz.sunqian.common.net.udp;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.common.net.GekNetException;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * UDP client interface.
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface GekUdpClient {

    /**
     * Returns new builder of {@link GekUdpClient}. The returned builder is based on {@link DatagramSocket}.
     *
     * @return new builder
     */
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Sends data packet.
     *
     * @param packet the data packet
     */
    void send(GekUdpPacket packet);

    /**
     * Returns bound address of this client.
     *
     * @return bound address of this client
     */
    InetAddress getAddress();

    /**
     * Returns bound port of this client.
     *
     * @return bound port of this client
     */
    int getPort();

    /**
     * Returns bound socket address of this client.
     *
     * @return bound socket address of this client
     */
    SocketAddress getSocketAddress();

    /**
     * Returns underlying object which implements this interface, such as {@link DatagramSocket}.
     *
     * @return underlying object
     */
    Object getSource();

    /**
     * Returns a new builder configured with this client.
     *
     * @return a new builder configured with this client
     */
    Builder toBuilder();

    /**
     * Builder for {@link GekUdpClient}, based on {@link DatagramSocket}.
     */
    class Builder {

        protected int port = 0;
        protected @Nullable InetAddress address;
        protected @Nullable Consumer<DatagramSocket> socketConfig;

        /**
         * Sets local port, maybe 0 to get an available one from system.
         *
         * @param port local port
         * @return this builder
         */
        public Builder port(int port) {
            this.port = port;
            return this;
        }

        /**
         * Sets local address.
         *
         * @param address local address
         * @return this builder
         */
        public Builder address(InetAddress address) {
            this.address = address;
            return this;
        }

        /**
         * Sets local host name.
         *
         * @param hostName local host name
         * @return this builder
         */
        public Builder hostName(String hostName) {
            try {
                this.address = InetAddress.getByName(hostName);
                return this;
            } catch (UnknownHostException e) {
                throw new GekNetException(e);
            }
        }

        /**
         * Sets other socket config.
         *
         * @param socketConfig other socket config
         * @return this builder
         */
        public Builder socketConfig(Consumer<DatagramSocket> socketConfig) {
            this.socketConfig = socketConfig;
            return this;
        }

        /**
         * Builds the client.
         *
         * @return built client
         */
        public GekUdpClient build() {
            return new SocketUdpClient(this);
        }

        private static final class SocketUdpClient implements GekUdpClient {

            private final int port;
            private final InetAddress address;
            private final @Nullable Consumer<DatagramSocket> socketConfig;
            private final DatagramSocket socket;

            private SocketUdpClient(Builder builder) {
                this.port = builder.port;
                this.address = builder.address;
                this.socketConfig = builder.socketConfig;
                this.socket = buildSocket();
            }

            @Override
            public synchronized void send(GekUdpPacket packet) {
                ByteBuffer buffer = packet.getData();
                DatagramPacket datagramPacket;
                if (buffer.hasArray()) {
                    datagramPacket = new DatagramPacket(buffer.array(), buffer.arrayOffset(), buffer.remaining());
                } else {
                    byte[] bytes = JieBuffer.read(buffer);
                    datagramPacket = new DatagramPacket(bytes, bytes.length);
                }
                datagramPacket.setSocketAddress(packet.getHeader().getInetSocketAddress());
                try {
                    socket.send(datagramPacket);
                } catch (IOException e) {
                    throw new GekNetException(e);
                }
            }

            @Override
            public InetAddress getAddress() {
                return socket.getLocalAddress();
            }

            @Override
            public int getPort() {
                return socket.getLocalPort();
            }

            @Override
            public SocketAddress getSocketAddress() {
                return socket.getLocalSocketAddress();
            }

            @Override
            public DatagramSocket getSource() {
                if (socket == null) {
                    throw new GekNetException("Server has not been initialized.");
                }
                return socket;
            }

            @Override
            public Builder toBuilder() {
                return newBuilder()
                    .port(port)
                    .address(address)
                    .socketConfig(socketConfig);
            }

            private DatagramSocket buildSocket() {
                try {
                    DatagramSocket socket;
                    if (address != null) {
                        socket = new DatagramSocket(port, address);
                    } else {
                        socket = new DatagramSocket(port);
                    }
                    if (socketConfig != null) {
                        socketConfig.accept(socket);
                    }
                    return socket;
                } catch (Exception e) {
                    throw new GekNetException(e);
                }
            }
        }
    }
}
