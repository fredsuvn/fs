package space.sunqian.common.net.tcp;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.Checker;
import space.sunqian.common.Fs;
import space.sunqian.common.io.IOKit;
import space.sunqian.common.io.IOOperator;
import space.sunqian.common.io.communicate.AbstractChannelContext;
import space.sunqian.common.net.NetException;

import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Builder for building new instances of {@link TcpClient} by {@link SocketChannel}.
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
        Checker.checkArgument(bufSize > 0, "bufSize must be positive");
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
        return Fs.uncheck(() -> new TcpClientImpl(
                localAddress,
                remoteAddress,
                socketOptions,
                bufSize
            ),
            NetException::new
        );
    }

    private static final class TcpClientImpl extends AbstractChannelContext<SocketChannel> implements TcpClient {

        // private final @Nonnull SocketChannel client;
        private final @Nonnull InetSocketAddress localAddress;
        private final @Nonnull InetSocketAddress remoteAddress;
        private final @Nonnull Selector selector;
        private final @Nonnull IOOperator ioOperator;

        private volatile boolean closed = false;

        @SuppressWarnings("resource")
        private TcpClientImpl(
            @Nullable InetSocketAddress localAddress,
            @Nonnull InetSocketAddress remoteAddress,
            @Nonnull Map<SocketOption<?>, Object> socketOptions,
            int bufSize
        ) throws Exception {
            super(SocketChannel.open());
            SocketChannel client = channel();
            this.remoteAddress = remoteAddress;
            socketOptions.forEach((name, value) ->
                Fs.uncheck(() -> client.setOption(Fs.as(name), value), NetException::new));
            this.selector = Selector.open();
            client.bind(localAddress);
            this.localAddress = (InetSocketAddress) client.getLocalAddress();
            client.configureBlocking(true);
            client.connect(remoteAddress);
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            this.ioOperator = IOOperator.get(bufSize);
        }

        @Override
        public synchronized void close() throws NetException {
            if (closed) {
                return;
            }
            Fs.uncheck(() -> {
                SocketChannel client = channel();
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

        @SuppressWarnings("resource")
        @Override
        public boolean isConnected() {
            SocketChannel client = channel();
            return client.isConnected();
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public void readWait() {
            Fs.uncheck(() -> {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keys = selectedKeys.iterator();
                while (keys.hasNext()) {
                    // SelectionKey key = keys.next();
                    keys.next();
                    // ignored
                    keys.remove();
                }
            }, NetException::new);
        }

        @Override
        public void readWakeUp() {
            selector.wakeup();
        }

        @Override
        protected @Nonnull IOOperator ioOperator() {
            return ioOperator;
        }
    }
}
