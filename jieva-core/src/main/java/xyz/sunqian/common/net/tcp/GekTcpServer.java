package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collection.JieCollection;
import xyz.sunqian.common.data.GekData;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.common.net.GekNetException;
import xyz.sunqian.common.net.GekNetServerException;
import xyz.sunqian.common.net.GekServerStates;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/**
 * TCP/IP server interface, server endpoint of {@link GekTcpEndpoint}. The implementation should use
 * {@link GekTcpChannel} to represents connection between server and remote endpoints. And should support following
 * types of callback handler:
 * <ul>
 *     <li>
 *         one {@link GekTcpServerHandler}: to callback for server events;
 *     </li>
 *     <li>
 *         a list of {@link GekTcpClientHandler}: to callback for connection events;
 *     </li>
 * </ul>
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface GekTcpServer extends GekTcpEndpoint {

    /**
     * Returns new builder of {@link GekTcpServer}. The returned builder is based on {@link ServerSocket}.
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
     * Returns a new builder configured with this server.
     *
     * @return a new builder configured with this server
     */
    Builder toBuilder();

    /**
     * Builder for {@link GekTcpServer}, based on {@link ServerSocket}.
     */
    class Builder {

        private static final byte[] EMPTY_ARRAY = new byte[0];
        private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(EMPTY_ARRAY);
        private static final GekTcpServerHandler EMPTY_SERVER_HANDLER = new GekTcpServerHandler() {
        };
        private final List<GekTcpChannelHandler<?>> channelHandlers = new LinkedList<>();
        private int port = 0;
        private int maxConnection = 50;
        private @Nullable InetAddress address;
        private @Nullable GekTcpServerHandler serverHandler;
        private @Nullable IntFunction<ByteBuffer> bufferGenerator;
        private @Nullable ExecutorService executor;
        private int channelBufferSize = JieIO.BUFFER_SIZE;
        private @Nullable Consumer<ServerSocket> socketConfig;

        /**
         * Sets server port, maybe 0 to get an available one from system.
         *
         * @param port server port
         * @return this builder
         */
        public Builder port(int port) {
            this.port = port;
            return this;
        }

        /**
         * Sets max connection number.
         *
         * @param maxConnection max connection number
         * @return this builder
         */
        public Builder maxConnection(int maxConnection) {
            this.maxConnection = maxConnection;
            return this;
        }

        /**
         * Sets server address.
         *
         * @param address server address
         * @return this builder
         */
        public Builder address(InetAddress address) {
            this.address = address;
            return this;
        }

        /**
         * Sets server host name.
         *
         * @param hostName server host name
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
         * Sets server handler.
         *
         * @param serverHandler server handler
         * @return this builder
         */
        public Builder serverHandler(GekTcpServerHandler serverHandler) {
            this.serverHandler = serverHandler;
            return this;
        }

        /**
         * Adds channel handler.
         *
         * @param channelHandler channel handler
         * @return this builder
         */
        public Builder addChannelHandler(GekTcpChannelHandler<?> channelHandler) {
            this.channelHandlers.add(channelHandler);
            return this;
        }

        /**
         * Adds channel handlers.
         *
         * @param channelHandlers channel handlers
         * @return this builder
         */
        public Builder addChannelHandlers(Iterable<GekTcpChannelHandler<?>> channelHandlers) {
            JieCollection.addAll(this.channelHandlers, channelHandlers);
            return this;
        }

        /**
         * Sets byte buffer generator: given an int returns a byte buffer with the int length. The generated buffer's
         * position must be 0, and limit must be capacity.
         *
         * @param bufferGenerator byte buffer generator
         * @return this builder
         */
        public Builder bufferGenerator(IntFunction<ByteBuffer> bufferGenerator) {
            this.bufferGenerator = bufferGenerator;
            return this;
        }

        /**
         * Sets executor service, must be of multi-threads.
         *
         * @param executor executor service
         * @return this builder
         */
        public Builder executor(ExecutorService executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Sets buffer size of the channel.
         *
         * @param channelBufferSize buffer size of the channel
         * @return this builder
         */
        public Builder channelBufferSize(int channelBufferSize) {
            this.channelBufferSize = channelBufferSize;
            return this;
        }

        /**
         * Sets other socket config.
         *
         * @param socketConfig other socket config
         * @return this builder
         */
        public Builder socketConfig(Consumer<ServerSocket> socketConfig) {
            this.socketConfig = socketConfig;
            return this;
        }

        /**
         * Builds the server.
         *
         * @return built server
         */
        public GekTcpServer build() {
            return new SocketTcpServer(this);
        }

        private static final class SocketTcpServer implements GekTcpServer {

            private final int port;
            private final int maxConnection;
            private final InetAddress address;
            private final GekTcpServerHandler serverHandler;
            private final List<GekTcpChannelHandler<?>> channelHandlers;
            private final IntFunction<ByteBuffer> bufferGenerator;
            private final ExecutorService executor;
            private final int channelBufferSize;
            private final @Nullable Consumer<ServerSocket> socketConfig;

            private final CountDownLatch latch = new CountDownLatch(1);
            private final GekServerStates state = new GekServerStates();
            private final Set<ChannelImpl> channels = ConcurrentHashMap.newKeySet();
            private @Nullable ServerSocket serverSocket;
            private volatile boolean outAcceptLoop = false;

            private SocketTcpServer(Builder builder) {
                this.port = builder.port;
                this.maxConnection = builder.maxConnection;
                this.address = builder.address;
                this.serverHandler = Jie.nonNull(builder.serverHandler, EMPTY_SERVER_HANDLER);
                this.channelHandlers = JieCollection.toList(builder.channelHandlers);
                if (channelHandlers.isEmpty()) {
                    throw new GekNetException("Channel handlers are empty.");
                }
                this.executor = builder.executor;
                if (executor == null) {
                    throw new GekNetException("Executor is null.");
                }
                this.socketConfig = builder.socketConfig;
                this.bufferGenerator = Jie.nonNull(builder.bufferGenerator, ByteBuffer::allocate);
                this.channelBufferSize = builder.channelBufferSize;
                if (channelBufferSize <= 0) {
                    throw new GekNetException("Channel buffer size must > 0.");
                }
            }

            @Override
            public synchronized void start(boolean block) {
                if (!state.isCreated()) {
                    throw new GekNetException("The server has been opened or closed.");
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
            public InetAddress getAddress() {
                if (serverSocket == null) {
                    throw new GekNetException("Server has not been initialized.");
                }
                return serverSocket.getInetAddress();
            }

            @Override
            public int getPort() {
                if (serverSocket == null) {
                    throw new GekNetException("Server has not been initialized.");
                }
                return serverSocket.getLocalPort();
            }

            @Override
            public SocketAddress getSocketAddress() {
                if (serverSocket == null) {
                    throw new GekNetException("Server has not been initialized.");
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
                } catch (GekNetException e) {
                    throw e;
                } catch (InterruptedException e) {
                    // do nothing
                } catch (Exception e) {
                    throw new GekNetException(e);
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
                } catch (GekNetException e) {
                    throw e;
                } catch (Exception e) {
                    throw new GekNetException(e);
                } finally {
                    state.close();
                }
            }

            @Override
            public ServerSocket getSource() {
                if (serverSocket == null) {
                    throw new GekNetException("Server has not been initialized.");
                }
                return serverSocket;
            }

            @Override
            public Builder toBuilder() {
                return newBuilder()
                    .port(port)
                    .address(address)
                    .maxConnection(maxConnection)
                    .serverHandler(serverHandler)
                    .addChannelHandlers(channelHandlers)
                    .bufferGenerator(bufferGenerator)
                    .executor(executor)
                    .channelBufferSize(channelBufferSize)
                    .socketConfig(socketConfig);
            }

            private void start0() {
                serverSocket = buildServerSocket();
                loopServerSocket();
            }

            private void close0() throws Exception {
                if (!state.isOpened() || serverSocket == null) {
                    throw new GekNetException("The server has not been opened.");
                }
                if (state.isClosed()) {
                    return;
                }
                serverSocket.close();
            }

            private ServerSocket buildServerSocket() {
                try {
                    ServerSocket server;
                    if (address != null) {
                        server = new ServerSocket(port, maxConnection, address);
                    } else {
                        server = new ServerSocket(port, maxConnection);
                    }
                    if (socketConfig != null) {
                        socketConfig.accept(server);
                    }
                    return server;
                } catch (Exception e) {
                    throw new GekNetException(e);
                }
            }

            private void loopServerSocket() {
                executor.execute(() -> {
                    while (!serverSocket.isClosed()) {
                        try {
                            Socket socket;
                            ChannelImpl channel;
                            try {
                                socket = serverSocket.accept();
                                channel = new ChannelImpl(socket);
                            } catch (Throwable e) {
                                executor.execute(() ->
                                    serverHandler.onException(new GekNetServerException(serverSocket, e)));
                                continue;
                            }
                            channels.add(channel);
                        } catch (Throwable e) {
                            // Ensure the loop continue
                        }
                    }
                    outAcceptLoop = true;
                });
                executor.execute(() -> {
                    while (true) {
                        Iterator<ChannelImpl> it = channels.iterator();
                        while (it.hasNext()) {
                            ChannelImpl channel = it.next();
                            if (channel.onClose) {
                                it.remove();
                            }
                            if (channel.lock) {
                                continue;
                            }
                            channel.lock = true;
                            try {
                                if (!channel.onOpen) {
                                    executor.execute(() -> {
                                        try {
                                            serverHandler.onOpen(channel);
                                        } catch (Throwable e) {
                                            serverHandler.onException(channel, e, EMPTY_BUFFER);
                                        } finally {
                                            channel.onOpen = true;
                                            channel.lock = false;
                                        }
                                    });
                                } else {
                                    executor.execute(() -> {
                                        try {
                                            doChannel(channel);
                                        } catch (Throwable e) {
                                            compactBuffer(channel);
                                            serverHandler.onException(channel, e, channel.buffer);
                                        } finally {
                                            channel.lock = false;
                                        }
                                    });
                                }
                            } catch (Throwable e) {
                                // Ensure the loop continue
                            }
                        }
                        if (outAcceptLoop && serverSocket.isClosed() && channels.isEmpty()) {
                            latch.countDown();
                            break;
                        }
                        Jie.sleep(1);
                    }
                });
            }

            private void doChannel(ChannelImpl channel) {
                if (channel.onClose) {
                    return;
                }
                byte[] newBytes = channel.availableOrClosed();
                if (newBytes == null) {
                    // null means channel closed or error
                    try {
                        channel.closeNow();
                        compactBuffer(channel);
                        serverHandler.onClose(channel, channel.buffer);
                    } catch (Throwable e) {
                        compactBuffer(channel);
                        serverHandler.onException(channel, e, channel.buffer);
                    } finally {
                        channel.onClose = true;
                    }
                    return;
                }
                if (newBytes.length == 0) {
                    compactBuffer(channel);
                    serverHandler.onLoop(channel, false, channel.buffer);
                    return;
                }
                compactBuffer(channel, newBytes);
                Object message = channel.buffer;
                for (GekTcpChannelHandler<?> channelHandler : channelHandlers) {
                    GekTcpChannelHandler<Object> handler = Jie.as(channelHandler);
                    Object result = handler.onMessage(channel, message);
                    if (result == null) {
                        break;
                    }
                    message = result;
                }
                compactBuffer(channel);
                serverHandler.onLoop(channel, true, channel.buffer);
            }

            private void compactBuffer(ChannelImpl channel) {
                channel.buffer = TcpUtils.compact(channel.buffer, bufferGenerator);
            }

            private void compactBuffer(ChannelImpl channel, byte[] newBytes) {
                channel.buffer = TcpUtils.compact(channel.buffer, newBytes, bufferGenerator);
            }

            private final class ChannelImpl implements GekTcpChannel {

                private final Socket socket;
                private volatile boolean lock = false;
                private volatile boolean onOpen = false;
                private volatile boolean onClose = false;
                private volatile ByteBuffer buffer = EMPTY_BUFFER;

                private volatile @Nullable OutputStream out;

                private ChannelImpl(Socket socket) {
                    this.socket = socket;
                }

                @Override
                public InetAddress getRemoteAddress() {
                    return socket.getInetAddress();
                }

                @Override
                public int getRemotePort() {
                    return socket.getPort();
                }

                @Override
                public InetAddress getLocalAddress() {
                    return socket.getLocalAddress();
                }

                @Override
                public int getLocalPort() {
                    return socket.getLocalPort();
                }

                @Override
                public SocketAddress getRemoteSocketAddress() {
                    return socket.getRemoteSocketAddress();
                }

                @Override
                public SocketAddress getLocalSocketAddress() {
                    return socket.getLocalSocketAddress();
                }

                @Override
                public boolean isOpened() {
                    return socket.isConnected();
                }

                @Override
                public boolean isClosed() {
                    return socket.isClosed();
                }

                @Override
                public synchronized void close(@Nullable Duration timeout) {
                    if (socket.isClosed()) {
                        return;
                    }
                    try {
                        getOutputStream().flush();
                    } catch (IOException e) {
                        throw new GekNetException(e);
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        throw new GekNetException(e);
                    }
                }

                @Override
                public synchronized void closeNow() {
                    if (socket.isClosed()) {
                        return;
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        throw new GekNetException(e);
                    }
                }

                @Override
                public synchronized void send(GekData data) {
                    JieIO.readTo(data.asInputStream(), getOutputStream());
                }

                @Override
                public synchronized void send(byte[] data) {
                    try {
                        getOutputStream().write(data);
                    } catch (IOException e) {
                        throw new GekNetException(e);
                    }
                }

                @Override
                public synchronized void send(byte[] data, int offset, int length) {
                    try {
                        getOutputStream().write(data, offset, length);
                    } catch (IOException e) {
                        throw new GekNetException(e);
                    }
                }

                @Override
                public synchronized void send(ByteBuffer data) {
                    if (data.hasArray()) {
                        send(data.array(), data.arrayOffset(), data.remaining());
                    } else {
                        send(JieBuffer.read(data));
                    }
                }

                @Override
                public synchronized void send(InputStream data) {
                    JieIO.readTo(data, getOutputStream());
                }

                @Override
                public synchronized void flush() {
                    try {
                        getOutputStream().flush();
                    } catch (IOException e) {
                        throw new GekNetException(e);
                    }
                }

                @Override
                public Object getSource() {
                    return socket;
                }

                private OutputStream getOutputStream() {
                    if (out == null) {
                        try {
                            out = socket.getOutputStream();
                        } catch (IOException e) {
                            throw new GekNetException(e);
                        }
                    }
                    return out;
                }

                @Nullable
                byte[] availableOrClosed() {
                    InputStream in;
                    try {
                        in = socket.getInputStream();
                    } catch (IOException e) {
                        return null;
                    }
                    int available;
                    try {
                        available = in.available();
                    } catch (IOException e) {
                        return null;
                    }
                    if (available == 0) {
                        if (socket.isClosed()) {
                            return null;
                        }
                        return EMPTY_ARRAY;
                    }
                    int maxRead = channelBufferSize - buffer.remaining();
                    int needRead = Math.min(available, maxRead);
                    if (needRead <= 0) {
                        return EMPTY_ARRAY;
                    }
                    byte[] newBytes = new byte[needRead];
                    int readCount;
                    try {
                        readCount = in.read(newBytes);
                    } catch (IOException e) {
                        return null;
                    }
                    if (readCount < newBytes.length) {
                        return Arrays.copyOf(newBytes, readCount);
                    }
                    return newBytes;
                }
            }
        }
    }
}
