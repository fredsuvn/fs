package xyz.sunqian.common.net.udp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.CheckKit;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.function.callable.VoidCallable;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.net.NetException;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

/**
 * Builder for building new instances of {@link UdpServer} by {@link DatagramChannel}.
 * <p>
 * The server built by this builder requires a main thread which is responsible for receiving datagram.
 *
 * @author sunqian
 */
public class UdpServerBuilder {

    private @Nonnull UdpServerHandler handler = UdpServerHandler.nullHandler();
    private @Nullable ThreadFactory mainThreadFactory;
    private int maxPacketSize = IOKit.bufferSize();
    private final @Nonnull Map<SocketOption<?>, Object> socketOptions = new LinkedHashMap<>();

    /**
     * Sets the handler to handle server events. The default handler is {@link UdpServerHandler#nullHandler()}.
     *
     * @param handler the handler to handle server events
     * @return this builder
     */
    public @Nonnull UdpServerBuilder handler(@Nonnull UdpServerHandler handler) {
        this.handler = handler;
        return this;
    }

    /**
     * Sets the main thread factory to create main thread. The main thread is responsible for accepting new client, and
     * then the worker thread will take over the already connected clients.
     * <p>
     * If the factory is not configured, the server will use {@link Thread#Thread(Runnable)}.
     *
     * @param mainThreadFactory the main thread factory
     * @return this builder
     */
    public @Nonnull UdpServerBuilder mainThreadFactory(@Nonnull ThreadFactory mainThreadFactory) {
        this.mainThreadFactory = mainThreadFactory;
        return this;
    }

    /**
     * Sets the max data packet size this server can receive. The default is {@link IOKit#bufferSize()}.
     *
     * @param maxPacketSize the max data packet size
     * @return this builder
     * @throws IllegalArgumentException If the max packet size is less than 1
     */
    public @Nonnull UdpServerBuilder maxPacketSize(int maxPacketSize) throws IllegalArgumentException {
        CheckKit.checkArgument(maxPacketSize >= 1, "maxPacketSize must >= 1");
        this.maxPacketSize = maxPacketSize;
        return this;
    }

    /**
     * Sets a socket option. This method can be invoked multiple times to set different socket options.
     *
     * @param <T>   the type of the socket option value
     * @param name  the socket option
     * @param value the value of the socket option, a value of {@code null} may be a valid value for some socket
     *              options.
     * @return this builder
     * @throws NetException If an error occurs
     * @see StandardSocketOptions
     */
    public <T> @Nonnull UdpServerBuilder socketOption(@Nonnull SocketOption<T> name, T value) throws NetException {
        socketOptions.put(name, value);
        return this;
    }

    /**
     * Binds the server's socket to the automatically assigned address and configures the socket to listen for
     * connections. And a new {@link UdpServer} instance is returned.
     *
     * @return a new {@link UdpServer} instance
     * @throws NetException If an error occurs
     */
    public @Nonnull UdpServer bind() throws NetException {
        return bind(null);
    }

    /**
     * Binds the server's socket to the specified local address and configures the socket to listen for connections. And
     * a new {@link UdpServer} instance is returned.
     *
     * @param localAddress the local address the server is bound to, may be {@code null} to bind to the automatically
     *                     assigned address
     * @return a new {@link UdpServer} instance
     * @throws NetException If an error occurs
     */
    public @Nonnull UdpServer bind(@Nullable InetSocketAddress localAddress) throws NetException {
        return Jie.uncheck(() -> new UdpServerImpl(
                localAddress,
                handler,
                mainThreadFactory,
                maxPacketSize,
                socketOptions
            ),
            NetException::new
        );
    }

    private static final class UdpServerImpl implements UdpServer, Runnable {

        private final @Nonnull DatagramChannel server;
        private final @Nonnull Selector mainSelector;
        private final @Nonnull Thread mainThread;
        private final @Nonnull UdpServerHandler handler;
        private final @Nonnull InetSocketAddress localAddress;
        private final @Nonnull ByteBuffer buffer;

        private volatile boolean closed = false;

        @SuppressWarnings("resource")
        private UdpServerImpl(
            @Nullable InetSocketAddress localAddress,
            @Nonnull UdpServerHandler handler,
            @Nullable ThreadFactory mainthreadFactory,
            int maxPacketSize,
            Map<SocketOption<?>, Object> socketOptions
        ) throws Exception {
            this.server = DatagramChannel.open();
            this.mainSelector = Selector.open();
            this.handler = handler;
            this.mainThread = newThread(mainthreadFactory, this);
            socketOptions.forEach((name, value) ->
                Jie.uncheck(() -> server.setOption(Jie.as(name), value), NetException::new));
            server.configureBlocking(false);
            server.register(mainSelector, SelectionKey.OP_READ);
            this.buffer = ByteBuffer.allocate(maxPacketSize);
            server.bind(localAddress);
            this.localAddress = (InetSocketAddress) server.getLocalAddress();
            mainThread.start();
        }

        private @Nonnull Thread newThread(@Nullable ThreadFactory factory, @Nonnull Runnable runnable) {
            return factory == null ? new Thread(runnable) : factory.newThread(runnable);
        }

        @Override
        public void await() throws NetException {
            try {
                mainThread.join();
            } catch (InterruptedException ignored) {
            }
        }

        @Override
        public synchronized void close() throws NetException {
            if (closed) {
                return;
            }
            Jie.uncheck(() -> {
                    server.close();
                    mainSelector.close();
                    mainSelector.wakeup();
                    mainThread.interrupt();
                },
                NetException::new
            );
            closed = true;
        }

        @Override
        public @Nonnull InetSocketAddress localAddress() throws NetException {
            return localAddress;
        }

        @Override
        public @Nonnull List<@Nonnull Worker> workers() {
            return Collections.emptyList();
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public void run() {
            while (!mainThread.isInterrupted()) {
                doWork(this::doMainWork, closed);
            }
            Jie.uncheck(() -> {
                server.close();
                mainSelector.close();
            }, NetException::new);
        }

        private void doMainWork() throws Exception {
            mainSelector.select();
            Set<SelectionKey> selectedKeys = mainSelector.selectedKeys();
            Iterator<SelectionKey> keys = selectedKeys.iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();
                handleRead(key);
                // key.cancel();
            }
        }

        private void handleRead(SelectionKey key) throws Exception {
            DatagramChannel channel = (DatagramChannel) key.channel();
            buffer.clear();
            SocketAddress address = channel.receive(buffer);
            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            BufferKit.readTo(buffer, data);
            UdpKit.channelRead(handler, channel, data, address);
        }

        private void doWork(VoidCallable callable, boolean closed) {
            if (closed) {
                return;
            }
            try {
                callable.call();
            } catch (Exception e) {
                handler.exceptionCaught(null, e);
            }
        }
    }
}
