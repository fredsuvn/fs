package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.CollectKit;
import xyz.sunqian.common.collect.ListKit;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.net.NetException;
import xyz.sunqian.common.net.GekServerStates;
import xyz.sunqian.common.net.data.GekData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/**
 * TCP/IP client interface, client endpoint of {@link GekTcpEndpoint}.
 * <p>
 * A {@link GekTcpClient} implementation should support following types of handler:
 * <ul>
 *     <li>
 *         one {@link GekTcpClientHandler}: to deal with client actions;
 *     </li>
 *     <li>
 *         a list of {@link GekTcpClientHandler}: to deal with actions for the connection;
 *     </li>
 * </ul>
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface GekTcpClient extends GekTcpEndpoint {

    /**
     * Returns new builder of {@link GekTcpClient}. The returned builder is based on {@link Socket}.
     *
     * @return new builder
     */
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Starts this client and blocks until the client has been closed. Connection timeout is 30 seconds.
     *
     * @param address address of target endpoint
     */
    default void start(SocketAddress address) {
        start(address, Duration.ofSeconds(30));
    }

    /**
     * Starts this client and blocks until the client has been closed. Connection timeout is 30 seconds.
     *
     * @param host host name of target endpoint
     * @param port port of target endpoint
     */
    default void start(String host, int port) {
        start(new InetSocketAddress(host, port), Duration.ofSeconds(30));
    }

    /**
     * Starts this client and blocks until the client has been closed.
     *
     * @param address address of target endpoint
     * @param timeout wait timeout to connect, maybe null to infinite waiting
     */
    void start(SocketAddress address, @Nullable Duration timeout);

    /**
     * Returns a new builder configured with this client.
     *
     * @return a new builder configured with this client
     */
    Builder toBuilder();

    /**
     * Builder for {@link GekTcpClient}, based on {@link Socket}.
     */
    class Builder {

        private static final byte[] EMPTY_ARRAY = new byte[0];
        private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(EMPTY_ARRAY);
        private static final GekTcpClientHandler EMPTY_CLIENT_HANDLER = new GekTcpClientHandler() {
        };
        private final List<GekTcpChannelHandler<?>> channelHandlers = new LinkedList<>();
        private int port = 0;
        private @Nullable InetAddress address;
        private @Nullable GekTcpClientHandler clientHandler;
        private @Nullable IntFunction<ByteBuffer> bufferGenerator;
        private int channelBufferSize = IOKit.bufferSize();
        private @Nullable Consumer<Socket> socketConfig;
        private @Nullable Proxy proxy;

        /**
         * Sets local port.
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
                throw new NetException(e);
            }
        }

        /**
         * Sets client handler.
         *
         * @param clientHandler client handler
         * @return this builder
         */
        public Builder clientHandler(GekTcpClientHandler clientHandler) {
            this.clientHandler = clientHandler;
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
            CollectKit.addAll(this.channelHandlers, channelHandlers);
            return this;
        }

        /**
         * Sets byte buffer generator: given an int returns a byte buffer with the int length. The generated buffer's
         * position must be 0, and limit must be capacity. *
         *
         * @param bufferGenerator byte buffer generator
         * @return this builder
         */
        public Builder bufferGenerator(IntFunction<ByteBuffer> bufferGenerator) {
            this.bufferGenerator = bufferGenerator;
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
        public Builder socketConfig(Consumer<Socket> socketConfig) {
            this.socketConfig = socketConfig;
            return this;
        }

        /**
         * Sets proxy
         *
         * @param proxy proxy
         * @return this builder
         */
        public Builder proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        /**
         * Builds the client.
         *
         * @return built client
         */
        public GekTcpClient build() {
            return new SocketTcpClient(this);
        }

        private static final class SocketTcpClient implements GekTcpClient {

            private final int port;
            private final InetAddress address;
            private final GekTcpClientHandler clientHandler;
            private final List<GekTcpChannelHandler<?>> channelHandlers;
            private final IntFunction<ByteBuffer> bufferGenerator;
            private final int channelBufferSize;
            private final @Nullable Consumer<Socket> socketConfig;
            private final @Nullable Proxy proxy;

            private final CountDownLatch latch = new CountDownLatch(1);
            private final GekServerStates state = new GekServerStates();
            private @Nullable Socket socket;

            private SocketTcpClient(Builder builder) {
                this.port = builder.port;
                this.address = builder.address;
                this.clientHandler = Jie.nonnull(builder.clientHandler, EMPTY_CLIENT_HANDLER);
                this.channelHandlers = ListKit.toList(builder.channelHandlers);
                if (channelHandlers.isEmpty()) {
                    throw new NetException("Channel handlers are empty.");
                }
                this.socketConfig = builder.socketConfig;
                this.bufferGenerator = Jie.nonnull(builder.bufferGenerator, ByteBuffer::allocate);
                this.channelBufferSize = builder.channelBufferSize;
                if (channelBufferSize <= 0) {
                    throw new NetException("Channel buffer size must > 0.");
                }
                this.proxy = builder.proxy;
            }

            @Override
            public void start(SocketAddress address, @Nullable Duration timeout) {
                synchronized (this) {
                    if (!state.isCreated()) {
                        throw new NetException("The client has been opened or closed.");
                    }
                }
                start0(address, timeout == null ? 0 : (int) timeout.toMillis());
            }

            @Override
            public InetAddress getAddress() {
                if (socket == null) {
                    throw new NetException("Client has not been initialized.");
                }
                return socket.getLocalAddress();
            }

            @Override
            public int getPort() {
                if (socket == null) {
                    throw new NetException("Client has not been initialized.");
                }
                return socket.getLocalPort();
            }

            @Override
            public SocketAddress getSocketAddress() {
                if (socket == null) {
                    throw new NetException("Client has not been initialized.");
                }
                return socket.getLocalSocketAddress();
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
            public Socket getSource() {
                if (socket == null) {
                    throw new NetException("Client has not been initialized.");
                }
                return socket;
            }

            @Override
            public Builder toBuilder() {
                return newBuilder()
                    .port(port)
                    .address(address)
                    .proxy(proxy)
                    .clientHandler(clientHandler)
                    .addChannelHandlers(channelHandlers)
                    .bufferGenerator(bufferGenerator)
                    .channelBufferSize(channelBufferSize)
                    .socketConfig(socketConfig);
            }

            private void start0(SocketAddress address, int timeout) {
                this.socket = buildSocket();
                try {
                    socket.connect(address, timeout);
                } catch (IOException e) {
                    throw new NetException(e);
                }
                state.open();
                ChannelImpl channel = new ChannelImpl();
                clientHandler.onOpen(channel);
                while (!socket.isClosed()) {
                    try {
                        doChannel(channel);
                    } catch (Throwable e) {
                        compactBuffer(channel);
                        clientHandler.onException(channel, e, channel.buffer);
                    }
                }
                try {
                    compactBuffer(channel);
                    clientHandler.onClose(channel, channel.buffer);
                } catch (Throwable e) {
                    compactBuffer(channel);
                    clientHandler.onException(channel, e, channel.buffer);
                } finally {
                    latch.countDown();
                }
            }

            private void close0() throws Exception {
                if (!state.isOpened() || socket == null) {
                    throw new NetException("The Client has not been opened.");
                }
                if (state.isClosed()) {
                    return;
                }
                socket.close();
            }

            private Socket buildSocket() {
                try {
                    Socket socket;
                    if (proxy != null) {
                        socket = new Socket(proxy);
                    } else {
                        socket = new Socket();
                    }
                    socket.bind(new InetSocketAddress(address, port));
                    if (socketConfig != null) {
                        socketConfig.accept(socket);
                    }
                    return socket;
                } catch (Exception e) {
                    throw new NetException(e);
                }
            }

            private void doChannel(ChannelImpl channel) {
                byte[] newBytes = channel.availableOrClosed();
                // null means channel closed or error
                if (newBytes == null) {
                    return;
                }
                if (newBytes.length == 0) {
                    compactBuffer(channel);
                    clientHandler.onLoop(channel, false, channel.buffer);
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
                clientHandler.onLoop(channel, true, channel.buffer);
            }

            private void compactBuffer(ChannelImpl channel) {
                channel.buffer = TcpUtils.compact(channel.buffer, bufferGenerator);
            }

            private void compactBuffer(ChannelImpl channel, byte[] newBytes) {
                channel.buffer = TcpUtils.compact(channel.buffer, newBytes, bufferGenerator);
            }

            private final class ChannelImpl implements GekTcpChannel {

                private volatile ByteBuffer buffer = EMPTY_BUFFER;
                private volatile @Nullable OutputStream out;

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
                    if (out != null) {
                        try {
                            out.flush();
                        } catch (IOException e) {
                            throw new NetException(e);
                        }
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        throw new NetException(e);
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
                        throw new NetException(e);
                    }
                }

                @Override
                public synchronized void send(GekData data) {
                    IOKit.readTo(data.asInputStream(), getOutputStream());
                }

                @Override
                public synchronized void send(byte[] data) {
                    try {
                        getOutputStream().write(data);
                    } catch (IOException e) {
                        throw new NetException(e);
                    }
                }

                @Override
                public synchronized void send(byte[] data, int offset, int length) {
                    try {
                        getOutputStream().write(data, offset, length);
                    } catch (IOException e) {
                        throw new NetException(e);
                    }
                }

                @Override
                public synchronized void send(ByteBuffer data) {
                    if (data.hasArray()) {
                        send(data.array(), data.arrayOffset(), data.remaining());
                    } else {
                        send(BufferKit.read(data));
                    }
                }

                @Override
                public synchronized void send(InputStream data) {
                    IOKit.readTo(data, getOutputStream());
                }

                @Override
                public synchronized void flush() {
                    try {
                        getOutputStream().flush();
                    } catch (IOException e) {
                        throw new NetException(e);
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
                            throw new NetException(e);
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
