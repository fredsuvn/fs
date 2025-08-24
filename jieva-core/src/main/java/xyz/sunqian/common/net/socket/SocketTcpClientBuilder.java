package xyz.sunqian.common.net.socket;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.CheckKit;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.communicate.IOChannel;
import xyz.sunqian.common.net.NetException;

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
 * Builder for building new instances of {@link TcpClient} by Socket.
 * <p>
 * Note the {@link TcpClient#ioChannel()} of the built instance is always in non-blocking mode, using
 * {@link TcpClient#nextRead()} can check whether there is data to read.
 *
 * @author sunqian
 */
public class SocketTcpClientBuilder {

    private @Nullable InetSocketAddress localAddress;
    private InetSocketAddress remoteAddress;
    private int bufSize = IOKit.bufferSize();
    private final @Nonnull Map<SocketOption<?>, Object> socketOptions = new LinkedHashMap<>();

    /**
     * Sets the local address the client is bound to. If the local address is not configured, the client will auto bind
     * to an available address when it starts.
     *
     * @param localAddress the local address the client is bound to
     * @return this builder
     */
    public @Nonnull SocketTcpClientBuilder localAddress(@Nonnull InetSocketAddress localAddress) {
        this.localAddress = localAddress;
        return this;
    }

    /**
     * Sets the remote address the client connects to.
     *
     * @param remoteAddress the remote address the client connects to
     * @return this builder
     */
    public @Nonnull SocketTcpClientBuilder remoteAddress(@Nonnull InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
        return this;
    }

    /**
     * Sets the buffer size for advanced IO operations. Note this buffer size is not the kernel network buffer size, it
     * is an I/O advanced operations buffer size.
     *
     * @param bufSize the buffer size for advanced IO operations
     * @return this builder
     * @throws IllegalArgumentException if the buffer size is negative or {@code 0}
     */
    public @Nonnull SocketTcpClientBuilder bufferSize(int bufSize) throws IllegalArgumentException {
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
    public <T> @Nonnull SocketTcpClientBuilder socketOption(@Nonnull SocketOption<T> name, T value) throws NetException {
        socketOptions.put(name, value);
        return this;
    }

    /**
     * Builds a new {@link TcpClient} with the configurations. Note the {@link TcpClient#ioChannel()} of the built
     * instance is always in non-blocking mode, using {@link TcpClient#nextRead()} can check whether there is data to
     * read.
     *
     * @return a new {@link TcpClient} with the configurations
     * @throws NetException If an error occurs
     */
    public @Nonnull TcpClient build() throws NetException {
        if (remoteAddress == null) {
            throw new NetException("The remote address can not be null.");
        }
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
        private volatile @Nullable InetSocketAddress localAddress;
        private final @Nonnull InetSocketAddress remoteAddress;
        private final @Nonnull Selector selector;
        private final @Nonnull IOChannel ioChannel;

        // 0: not started, 1: started, 2: closed
        private volatile int state = 0;

        private TcpClientImpl(
            @Nullable InetSocketAddress localAddress,
            @Nonnull InetSocketAddress remoteAddress,
            @Nonnull Map<SocketOption<?>, Object> socketOptions,
            int bufSize
        ) throws Exception {
            this.client = SocketChannel.open();
            this.localAddress = localAddress;
            this.remoteAddress = remoteAddress;
            SocketKit.setSocketOptions(socketOptions, client);
            this.selector = Selector.open();
            this.ioChannel = IOChannel.newChannel(client, bufSize);
        }

        @Override
        public synchronized void connect() throws NetException {
            if (state == 1) {
                throw new NetException("This client has already connected.");
            }
            if (state == 2) {
                throw new NetException("This client has already closed.");
            }
            state = 1;
            Jie.uncheck(() -> {
                client.bind(localAddress);
                client.configureBlocking(true);
                client.connect(remoteAddress);
                this.localAddress = (InetSocketAddress) client.getLocalAddress();
                client.configureBlocking(false);
                client.register(selector, SelectionKey.OP_READ);
            }, NetException::new);
        }

        @Override
        public synchronized void close() throws NetException {
            if (state != 1) {
                return;
            }
            Jie.uncheck(() -> {
                client.close();
                selector.close();
            }, NetException::new);
            state = 2;
        }

        @Override
        public @Nonnull InetSocketAddress remoteAddress() {
            return remoteAddress;
        }

        @Override
        public @Nullable InetSocketAddress localAddress() {
            return localAddress;
        }

        @Override
        public boolean isConnected() {
            return client.isConnected();
        }

        @Override
        public boolean isClosed() {
            return state == 2;
        }

        @Override
        public @Nonnull IOChannel ioChannel() {
            return ioChannel;
        }

        @Override
        public void nextRead() {
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
        public void wakeUpRead() {
            selector.wakeup();
        }
    }
}
