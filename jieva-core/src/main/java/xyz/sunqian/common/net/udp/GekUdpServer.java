package xyz.sunqian.common.net.udp;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.CollectKit;
import xyz.sunqian.common.collect.ListKit;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.net.GekServerStates;
import xyz.sunqian.common.net.NetException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * UDP server interface. The implementation should use {@link GekUdpPacket} to represents datagram packet of UDP. And
 * should support following types of callback handler:
 * <ul>
 *     <li>
 *         one {@link GekUdpServerHandler}: to callback for server events;
 *     </li>
 *     <li>
 *         a list of {@link GekUdpPacketHandler}: to callback for received packet events;
 *     </li>
 * </ul>
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface GekUdpServer extends GekUdpClient {

    /**
     * Returns new builder of {@link GekUdpServer}. The returned builder is based on {@link DatagramSocket}.
     *
     * @return new builder
     */
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Starts this server and wait until the server and all connections have been closed. This method is equivalent to
     * {@link #start(boolean)}:
     * <pre>
     *     start(true);
     * </pre>
     */
    default void start() {
        start(true);
    }

    /**
     * Starts this server. If given {@code block} is true, this method will block current thread until the server and
     * all connections have been closed.
     *
     * @param block whether block current thread
     */
    void start(boolean block);

    /**
     * Returns bound address of this server.
     *
     * @return bound address of this server
     */
    InetAddress getAddress();

    /**
     * Returns bound port of this server.
     *
     * @return bound port of this server
     */
    int getPort();

    /**
     * Returns bound socket address of this server.
     *
     * @return bound socket address of this server
     */
    SocketAddress getSocketAddress();

    /**
     * Returns underlying object which implements this interface, such as {@link DatagramSocket}.
     *
     * @return underlying object
     */
    Object getSource();

    /**
     * Returns whether this server is opened.
     *
     * @return whether this server is opened
     */
    boolean isOpened();

    /**
     * Returns whether this server is closed.
     *
     * @return whether this server is closed
     */
    boolean isClosed();

    /**
     * Closes this server, blocks current thread for buffered operations.
     */
    default void close() {
        close(null);
    }

    /**
     * Closes this server, blocks current thread for buffered operations in given timeout.
     *
     * @param timeout given timeout, maybe null to always wait
     */
    void close(@Nullable Duration timeout);

    /**
     * Closes this server immediately, without blocking and buffered operations.
     */
    void closeNow();

    /**
     * Returns a new builder configured with this server.
     *
     * @return a new builder
     */
    Builder toBuilder();

    /**
     * Builder for {@link GekUdpServer}, based on {@link DatagramSocket}.
     */
    class Builder extends GekUdpClient.Builder {

        private static final byte[] EMPTY_ARRAY = new byte[0];
        private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(EMPTY_ARRAY);
        private static final GekUdpServerHandler EMPTY_SERVER_HANDLER = new GekUdpServerHandler() {
        };
        private final List<GekUdpPacketHandler<?>> packetHandlers = new LinkedList<>();
        private @Nullable GekUdpServerHandler serverHandler;
        private @Nullable ExecutorService executor;
        private int packetBufferSize = IOKit.bufferSize();

        /**
         * Sets local port, maybe 0 to get an available one from system.
         *
         * @return this builder
         */
        @Override
        public Builder port(int port) {
            super.port(port);
            return this;
        }

        /**
         * Sets local address.
         *
         * @return this builder
         */
        @Override
        public Builder address(InetAddress address) {
            super.address(address);
            return this;
        }

        /**
         * Sets local host name.
         *
         * @return this builder
         */
        @Override
        public Builder hostName(String hostName) {
            super.hostName(hostName);
            return this;
        }

        /**
         * Sets other socket config.
         *
         * @return this builder
         */
        @Override
        public Builder socketConfig(Consumer<DatagramSocket> socketConfig) {
            super.socketConfig(socketConfig);
            return this;
        }

        /**
         * Sets server handler.
         *
         * @param serverHandler server handler
         * @return this builder
         */
        public Builder serverHandler(GekUdpServerHandler serverHandler) {
            this.serverHandler = serverHandler;
            return this;
        }

        /**
         * Adds packet handler.
         *
         * @param packetHandler packet handler
         * @return this builder
         */
        public Builder addPacketHandler(GekUdpPacketHandler<?> packetHandler) {
            this.packetHandlers.add(packetHandler);
            return this;
        }

        /**
         * Adds packet handlers.
         *
         * @param packetHandlers packet handlers
         * @return this builder
         */
        public Builder addPacketHandlers(Iterable<GekUdpPacketHandler<?>> packetHandlers) {
            CollectKit.addAll(this.packetHandlers, packetHandlers);
            return this;
        }

        /**
         * Sets executor, must be of multi-threads.
         *
         * @param executor executor, must be of multi-threads
         * @return this builder
         */
        public Builder executor(ExecutorService executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Sets buffer size of the packet.
         *
         * @param packetBufferSize buffer size of the packet
         * @return this builder
         */
        public Builder packetBufferSize(int packetBufferSize) {
            this.packetBufferSize = packetBufferSize;
            return this;
        }

        /**
         * Builds the server.
         *
         * @return built server
         */
        public GekUdpServer build() {
            return new SocketUdpServer(this);
        }

        private static final class SocketUdpServer implements GekUdpServer, GekUdpClient {

            private final int port;
            private final InetAddress address;
            private final GekUdpServerHandler serverHandler;
            private final List<GekUdpPacketHandler<?>> packetHandlers;
            private final ExecutorService executor;
            private final int packetBufferSize;
            private final @Nullable Consumer<DatagramSocket> socketConfig;

            private final CountDownLatch latch = new CountDownLatch(1);
            private final AtomicInteger packetCounter = new AtomicInteger();
            private final GekServerStates state = new GekServerStates();
            private @Nullable DatagramSocket serverSocket;

            private SocketUdpServer(GekUdpServer.Builder builder) {
                this.port = builder.port;
                this.address = builder.address;
                this.serverHandler = Jie.nonnull(builder.serverHandler, EMPTY_SERVER_HANDLER);
                this.packetHandlers = ListKit.toList(builder.packetHandlers);
                if (packetHandlers.isEmpty()) {
                    throw new NetException("Packet handlers are empty.");
                }
                this.executor = builder.executor;
                if (executor == null) {
                    throw new NetException("Executor is null.");
                }
                this.socketConfig = builder.socketConfig;
                this.packetBufferSize = builder.packetBufferSize;
                if (packetBufferSize <= 0) {
                    throw new NetException("Packet buffer size must > 0.");
                }
            }

            @Override
            public synchronized void start(boolean block) {
                if (!state.isCreated()) {
                    throw new NetException("The server has been opened or closed.");
                }
                start0();
                state.open();
                if (block) {
                    try {
                        latch.await();
                    } catch (InterruptedException ignored) {
                    }
                }
            }

            @Override
            public synchronized void send(GekUdpPacket packet) {
                ByteBuffer buffer = packet.getData();
                DatagramPacket datagramPacket;
                if (buffer.hasArray()) {
                    datagramPacket = new DatagramPacket(buffer.array(), buffer.arrayOffset(), buffer.remaining());
                } else {
                    byte[] bytes = BufferKit.read(buffer);
                    datagramPacket = new DatagramPacket(bytes, bytes.length);
                }
                datagramPacket.setSocketAddress(packet.getHeader().getInetSocketAddress());
                try {
                    serverSocket.send(datagramPacket);
                } catch (IOException e) {
                    throw new NetException(e);
                }
            }

            @Override
            public InetAddress getAddress() {
                if (serverSocket == null) {
                    throw new NetException("Server has not been initialized.");
                }
                return serverSocket.getLocalAddress();
            }

            @Override
            public int getPort() {
                if (serverSocket == null) {
                    throw new NetException("Server has not been initialized.");
                }
                return serverSocket.getLocalPort();
            }

            @Override
            public SocketAddress getSocketAddress() {
                if (serverSocket == null) {
                    throw new NetException("Server has not been initialized.");
                }
                return serverSocket.getLocalSocketAddress();
            }

            @Override
            public boolean isOpened() {
                return state.isOpened();
            }

            @Override
            public boolean isClosed() {
                return state.isClosed();
            }

            @Override
            public synchronized void close(@Nullable Duration timeout) {
                try {
                    close0();
                    if (timeout == null) {
                        latch.await();
                    } else {
                        latch.await(timeout.toMillis(), TimeUnit.MILLISECONDS);
                    }
                } catch (NetException e) {
                    throw e;
                } catch (InterruptedException e) {
                    // do nothing
                } catch (Exception e) {
                    throw new NetException(e);
                } finally {
                    state.close();
                }
            }

            @Override
            public synchronized void closeNow() {
                try {
                    close0();
                    executor.shutdown();
                    latch.countDown();
                } catch (NetException e) {
                    throw e;
                } catch (Exception e) {
                    throw new NetException(e);
                } finally {
                    state.close();
                }
            }

            @Override
            public DatagramSocket getSource() {
                if (serverSocket == null) {
                    throw new NetException("Server has not been initialized.");
                }
                return serverSocket;
            }

            @Override
            public GekUdpServer.Builder toBuilder() {
                return newBuilder()
                    .port(port)
                    .address(address)
                    .serverHandler(serverHandler)
                    .addPacketHandlers(packetHandlers)
                    .executor(executor)
                    .packetBufferSize(packetBufferSize)
                    .socketConfig(socketConfig);
            }

            private void start0() {
                serverSocket = buildServerSocket();
                loopServerSocket();
            }

            private void close0() {
                if (!state.isOpened() || serverSocket == null) {
                    throw new NetException("The server has not been opened.");
                }
                if (state.isClosed()) {
                    return;
                }
                serverSocket.close();
            }

            private DatagramSocket buildServerSocket() {
                try {
                    DatagramSocket server;
                    if (address != null) {
                        server = new DatagramSocket(port, address);
                    } else {
                        server = new DatagramSocket(port);
                    }
                    if (socketConfig != null) {
                        socketConfig.accept(server);
                    }
                    return server;
                } catch (Exception e) {
                    throw new NetException(e);
                }
            }

            private void loopServerSocket() {
                executor.execute(() -> {
                    byte[] data = new byte[packetBufferSize];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    while (!serverSocket.isClosed()) {
                        try {
                            serverSocket.receive(packet);
                            GekUdpPacket udpPacket = GekUdpPacket.from(packet);
                            ByteBuffer buffer = udpPacket.getData().asReadOnlyBuffer();
                            packetCounter.incrementAndGet();
                            executor.execute(() -> {
                                try {
                                    doPacket(udpPacket.getHeader(), buffer);
                                } catch (Throwable e) {
                                    serverHandler.onException(udpPacket.getHeader(), this, e, compactBuffer(buffer));
                                } finally {
                                    packetCounter.decrementAndGet();
                                }
                            });
                        } catch (Throwable e) {
                            // Ensure the loop continue
                        }
                    }
                    while (packetCounter.get() > 0) {
                        Jie.sleep(1);
                    }
                    latch.countDown();
                });
            }

            private void doPacket(GekUdpHeader header, ByteBuffer buffer) {
                Object packet = buffer;
                for (GekUdpPacketHandler<?> packetHandler : packetHandlers) {
                    GekUdpPacketHandler<Object> handler = Jie.as(packetHandler);
                    Object result = handler.onPacket(header, this, packet);
                    if (result == null) {
                        break;
                    }
                    packet = result;
                }
            }

            private ByteBuffer compactBuffer(ByteBuffer buffer) {
                if (buffer.position() == 0) {
                    return buffer;
                }
                if (!buffer.hasRemaining()) {
                    return EMPTY_BUFFER;
                }
                return ByteBuffer.wrap(BufferKit.read(buffer)).asReadOnlyBuffer();
            }
        }
    }
}
