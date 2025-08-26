package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.CheckKit;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.communicate.AbstractIOChannel;
import xyz.sunqian.common.net.NetException;

import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Builder for building new instances of {@link TcpClient} by Socket.
 *
 * @author sunqian
 */
public class TcpClientBuilder {

    private @Nullable InetSocketAddress localAddress;
    private int bufSize = IOKit.bufferSize();
    private final @Nonnull Map<SocketOption<?>, Object> socketOptions = new LinkedHashMap<>();

    /**
     * Sets the buffer size for advanced IO operations. Note this buffer size is not the kernel network buffer size, it
     * is an I/O advanced operations buffer size.
     *
     * @param bufSize the buffer size for advanced IO operations
     * @return this builder
     * @throws IllegalArgumentException if the buffer size is negative or {@code 0}
     */
    public @Nonnull TcpClientBuilder bufferSize(int bufSize) throws IllegalArgumentException {
        CheckKit.checkArgument(bufSize > 0, "bufSize must be positive");
        this.bufSize = bufSize;
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
    public <T> @Nonnull TcpClientBuilder socketOption(@Nonnull SocketOption<T> name, T value) throws NetException {
        socketOptions.put(name, value);
        return this;
    }

    /**
     * Binds the client's socket to the specified local address.
     *
     * @param localAddress the local address the client is bound to, may be {@code null} to bind to the automatically
     *                     assigned address
     * @return this builder
     * @throws NetException If an error occurs
     */
    public @Nonnull TcpClientBuilder bind(@Nullable InetSocketAddress localAddress) {
        this.localAddress = localAddress;
        return this;
    }

    /**
     * Connects this client's socket to the specified remote address and configures the socket to listen for
     * connections. And a new {@link TcpClient} instance is returned.
     *
     * @param remoteAddress the remote address the client connects to
     * @return a new {@link TcpClient} with the configurations
     * @throws NetException If an error occurs
     */
    public @Nonnull TcpClient connect(@Nonnull InetSocketAddress remoteAddress) throws NetException {
        return Jie.uncheck(() -> new TcpClientImpl(
                localAddress,
                remoteAddress,
                socketOptions,
                bufSize
            ),
            NetException::new
        );
    }

    private static final class TcpClientImpl implements TcpClient {

        private final @Nonnull SocketChannel client;
        private final @Nonnull InetSocketAddress localAddress;
        private final @Nonnull InetSocketAddress remoteAddress;
        private final @Nonnull Selector selector;
        private final @Nonnull TcpClientChannel channel;

        private volatile boolean closed = false;

        @SuppressWarnings("resource")
        private TcpClientImpl(
            @Nullable InetSocketAddress localAddress,
            @Nonnull InetSocketAddress remoteAddress,
            @Nonnull Map<SocketOption<?>, Object> socketOptions,
            int bufSize
        ) throws Exception {
            this.client = SocketChannel.open();
            this.remoteAddress = remoteAddress;
            socketOptions.forEach((name, value) ->
                Jie.uncheck(() -> client.setOption(Jie.as(name), value), NetException::new));
            this.selector = Selector.open();
            this.channel = new ChannelImpl(client, bufSize);
            client.bind(localAddress);
            this.localAddress = (InetSocketAddress) client.getLocalAddress();
            client.configureBlocking(true);
            client.connect(remoteAddress);
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        }

        @Override
        public synchronized void close() throws NetException {
            if (closed) {
                return;
            }
            Jie.uncheck(() -> {
                client.close();
                client.keyFor(selector).cancel();
                selector.close();
            }, NetException::new);
            closed = true;
        }

        @Override
        public @Nonnull InetSocketAddress remoteAddress() {
            return remoteAddress;
        }

        @Override
        public @Nonnull InetSocketAddress localAddress() {
            return localAddress;
        }

        @Override
        public boolean isConnected() {
            return client.isConnected();
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public @Nonnull TcpClientChannel ioChannel() {
            return channel;
        }

        private final class ChannelImpl extends AbstractIOChannel implements TcpClientChannel {

            private ChannelImpl(@Nonnull ByteChannel channel, int bufSize) throws IllegalArgumentException {
                super(channel, bufSize);
            }

            @Override
            public void awaitReadable() {
                Jie.uncheck(() -> {
                    selector.select();
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keys = selectedKeys.iterator();
                    while (keys.hasNext()) {
                        SelectionKey key = keys.next();
                        keys.remove();
                        // ignored
                    }
                }, NetException::new);
            }

            @Override
            public void wakeUpReadable() {
                selector.wakeup();
            }
        }
    }
}
