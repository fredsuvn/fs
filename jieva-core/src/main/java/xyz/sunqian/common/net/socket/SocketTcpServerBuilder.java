package xyz.sunqian.common.net.socket;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.CheckKit;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.communicate.IOChannel;
import xyz.sunqian.common.net.NetChannelContext;
import xyz.sunqian.common.net.NetChannelHandler;
import xyz.sunqian.common.net.NetChannelHandlerWrapper;
import xyz.sunqian.common.net.NetChannelType;
import xyz.sunqian.common.net.NetException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

/**
 * Builder for builder a TCP/IP network server by Socket.
 *
 * @author sunqian
 */
public class SocketTcpServerBuilder {

    private @Nullable InetSocketAddress localAddress;
    private @Nullable NetChannelHandlerWrapper handler;
    private int workThreadNum;
    private @Nullable ThreadFactory threadFactory;
    private int backlog = -1;
    private int bufSize = IOKit.bufferSize();
    private final Map<SocketOption<?>, Object> socketOptions = new LinkedHashMap<>();

    /**
     * Sets the local address the server is bound to. If the local address is not configured, the server will auto bind
     * to an available address when it starts.
     *
     * @param localAddress the local address the server is bound to
     * @return this builder
     */
    public @Nonnull SocketTcpServerBuilder localAddress(@Nonnull InetSocketAddress localAddress) {
        this.localAddress = localAddress;
        return this;
    }

    /**
     * Sets the handler to handle server events. The main behavior of the server is handled by this handler.
     *
     * @param handler the handler to handle server events
     * @return this builder
     */
    public @Nonnull SocketTcpServerBuilder handler(@Nonnull NetChannelHandler handler) {
        this.handler = new NetChannelHandlerWrapper(handler);
        return this;
    }

    /**
     * Sets the number of work thread. The server will use the worker thread handles the data exchange with the client.
     *
     * @param workThreadNum the number of work thread, must {@code >= 1}
     * @return this builder
     * @throws IllegalArgumentException if {@code < 1}
     */
    public @Nonnull SocketTcpServerBuilder workThreadNum(int workThreadNum) throws IllegalArgumentException {
        CheckKit.checkArgument(workThreadNum >= 1, "workThreadNum must >= 1");
        this.workThreadNum = workThreadNum;
        return this;
    }

    /**
     * Sets the thread factory to create work thread. If the factory is not configured, the server will use
     * {@link Thread#Thread(Runnable)} to create work thread.
     *
     * @param threadFactory the thread factory to create work thread
     * @return this builder
     */
    public @Nonnull SocketTcpServerBuilder threadFactory(@Nonnull ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    /**
     * Sets the {@code backlog}. The {@code backlog} is the maximum number of pending connections on the socket. Its
     * exact semantics are implementation specific. In particular, an implementation may impose a maximum length or may
     * choose to ignore. If the {@code backlog} parameter has the value 0, or a negative value, then an implementation
     * specific default is used.
     *
     * @param backlog the maximum number of pending connections
     * @return this builder
     */
    public @Nonnull SocketTcpServerBuilder backlog(int backlog) {
        this.backlog = backlog;
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
    public @Nonnull SocketTcpServerBuilder bufferSize(int bufSize) throws IllegalArgumentException {
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
    public <T> @Nonnull SocketTcpServerBuilder socketOption(@Nonnull SocketOption<T> name, T value) throws NetException {
        socketOptions.put(name, value);
        return this;
    }

    /**
     * Builds a new {@link TcpServer} with the configurations.
     *
     * @return a new {@link TcpServer} with the configurations
     * @throws NetException If an error occurs
     */
    public @Nonnull TcpServer build() throws NetException {
        if (handler == null) {
            throw new NetException("Handle can not be null.");
        }
        return new TcpServerImpl(localAddress, handler, workThreadNum, threadFactory, socketOptions, backlog, bufSize);
    }

    private static final class TcpServerImpl implements TcpServer {

        private final @Nonnull ServerSocketChannel server = Jie.uncheck(ServerSocketChannel::open, NetException::new);
        private final @Nonnull Selector bossSelector = Jie.uncheck(Selector::open, NetException::new);
        private final @Nonnull Worker @Nonnull [] workers;
        private final @Nonnull NetChannelHandlerWrapper handler;
        private final int bufSize;

        private volatile @Nullable Thread bossThread;
        private volatile boolean closed = false;

        private TcpServerImpl(
            @Nullable InetSocketAddress localAddress,
            @Nonnull NetChannelHandlerWrapper handler,
            int workThreadNum,
            @Nullable ThreadFactory threadFactory,
            Map<SocketOption<?>, Object> socketOptions,
            int backlog,
            int bufSize
        ) throws NetException {
            this.handler = handler;
            this.workers = new Worker[workThreadNum];
            this.bufSize = bufSize;
            Jie.uncheck(
                () -> init(localAddress, workThreadNum, threadFactory, socketOptions, backlog),
                NetException::new
            );
        }

        private void init(
            @Nullable InetSocketAddress localAddress,
            int workThreadNum,
            @Nullable ThreadFactory threadFactory,
            Map<SocketOption<?>, Object> socketOptions,
            int backlog
        ) throws Exception {
            server.bind(localAddress, backlog);
            server.configureBlocking(false);
            socketOptions.forEach((name, value) -> {
                try {
                    server.setOption(Jie.as(name), value);
                } catch (Exception e) {
                    throw new NetException(e);
                }
            });
            server.register(bossSelector, SelectionKey.OP_ACCEPT);
            for (int i = 0; i < workThreadNum; i++) {
                Worker worker = new Worker();
                workers[i] = worker;
                worker.thread = threadFactory == null ? new Thread(worker) : threadFactory.newThread(worker);
            }
        }

        @Override
        public synchronized void start() throws NetException {
            if (closed) {
                throw new NetException("This server has already closed.");
            }
            Thread thread = Thread.currentThread();
            this.bossThread = thread;
            for (Worker worker : workers) {
                worker.thread.start();
            }
            while (!thread.isInterrupted()) {
                if (closed) {
                    break;
                }
                try {
                    bossSelector.select();
                    Set<SelectionKey> selectedKeys = bossSelector.selectedKeys();
                    Iterator<SelectionKey> keys = selectedKeys.iterator();
                    while (keys.hasNext()) {
                        SelectionKey key = keys.next();
                        keys.remove();
                        handleAccept(key, workers);
                    }
                } catch (Exception e) {
                    handler.exceptionCaught(null, e);
                }
            }
            releaseWorkers();
        }

        @Override
        public synchronized void close() throws NetException {
            if (closed) {
                return;
            }
            closed = true;
            Jie.uncheck(server::close, NetException::new);
            Thread boss = bossThread;
            if (boss != null) {
                boss.interrupt();
                return;
            }
            bossSelector.wakeup();
            releaseWorkers();
        }

        private void releaseWorkers() {
            for (Worker worker : workers) {
                worker.thread.interrupt();
                worker.selector.wakeup();
            }
        }

        @Override
        public void await() throws NetException {
            Thread boss = bossThread;
            if (boss == null) {
                return;
            }
            try {
                boss.join();
            } catch (InterruptedException ignored) {
            }
        }

        @Override
        public @Nonnull InetSocketAddress localAddress() throws NetException {
            return (InetSocketAddress) Jie.uncheck(server::getLocalAddress, NetException::new);
        }

        @SuppressWarnings("resource")
        private void handleAccept(SelectionKey key, Worker[] workers) throws Exception {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            TcpContext context = new TcpContext(client);
            int index = findWorker(workers);
            workers[index].registerClient(context);
            workers[index].selector.wakeup();
        }

        private int findWorker(Worker[] workers) {
            int index = 0;
            int minClientCount = Integer.MAX_VALUE;
            for (int i = 0; i < workers.length; i++) {
                int clientCount = workers[i].clientSet.size();
                if (clientCount < minClientCount) {
                    minClientCount = clientCount;
                    index = i;
                }
            }
            return index;
        }

        private final class Worker implements Runnable {

            private final @Nonnull Selector selector;
            private final @Nonnull Set<TcpContext> clientSet = new HashSet<>();

            // the thread this worker starts on
            private Thread thread;

            private volatile @Nullable ClientNode clientNode;

            {
                try {
                    selector = Selector.open();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            //@SuppressWarnings({"InfiniteLoopStatement"})
            @Override
            public void run() {
                Thread thread = Thread.currentThread();
                while (!thread.isInterrupted()) {
                    if (closed) {
                        break;
                    }
                    try {
                        doWork();
                    } catch (Exception e) {
                        handler.exceptionCaught(null, e);
                    }
                }
                releaseClients();
            }

            private void doWork() throws Exception {
                // register client
                ClientNode head = this.clientNode;
                if (head != null) {
                    handleOpen(head);
                }
                // handle
                handleRead();
                // remove closed client
                handleClose();
            }

            private void handleOpen(@Nonnull ClientNode head) throws Exception {
                @Nonnull ClientNode node = head;
                while (true) {
                    if (!node.done) {
                        TcpContext context = node.context;
                        clientSet.add(context);
                        registerRead(context);
                        handler.channelOpen(context);
                        node.done = true;
                    }
                    ClientNode next = node.next;
                    if (next == null) {
                        this.clientNode = node;
                        break;
                    } else {
                        node = next;
                    }
                }
            }

            private void registerRead(TcpContext context) throws Exception {
                SocketChannel client = context.client;
                client.configureBlocking(false);
                client.register(selector, SelectionKey.OP_READ, context);
            }

            private void handleRead() throws Exception {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keys = selectedKeys.iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    TcpContext context = (TcpContext) key.attachment();
                    handler.channelRead(context);
                }
            }

            private void handleClose() {
                Set<TcpContext> rm = new HashSet<>();
                for (TcpContext context : clientSet) {
                    if (!context.client.isOpen()) {
                        rm.add(context);
                        context.close();
                    }
                }
                clientSet.removeAll(rm);
            }

            public void registerClient(TcpContext context) {
                ClientNode node = new ClientNode(context);
                ClientNode head = clientNode;
                if (head == null) {
                    clientNode = node;
                    return;
                }
                ClientNode cur = head;
                while (true) {
                    ClientNode next = cur.next;
                    if (next == null) {
                        cur.next = node;
                        return;
                    }
                    cur = next;
                }
            }

            private void releaseClients() {
                for (TcpContext context : clientSet) {
                    context.close();
                }
            }
        }

        private final class TcpContext implements NetChannelContext {

            private final @Nonnull SocketChannel client;
            private final @Nonnull InetSocketAddress remoteAddress;
            private final @Nonnull InetSocketAddress localAddress;
            private final @Nonnull IOChannel ioChannel;

            private boolean closed = false;

            private TcpContext(@Nonnull SocketChannel client) throws Exception {
                this.client = client;
                this.remoteAddress = (InetSocketAddress) client.getRemoteAddress();
                this.localAddress = (InetSocketAddress) server.getLocalAddress();
                this.ioChannel = IOChannel.newChannel(client, bufSize);
            }

            @Override
            public @Nonnull NetChannelType channelType() {
                return NetChannelType.tcpIp();
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
            public @Nonnull IOChannel ioChannel() {
                return ioChannel;
            }

            @Override
            public void close() throws NetException {
                if (closed) {
                    return;
                }
                closed = true;
                Jie.uncheck(client::close, NetException::new);
                handler.channelClose(this);
            }

            @Override
            public boolean isOpen() {
                return client.isOpen();
            }
        }

        private static final class ClientNode {

            private final @Nonnull TcpContext context;
            private volatile boolean done = false;
            private volatile @Nullable ClientNode next;

            private ClientNode(@Nonnull TcpContext context) {
                this.context = context;
            }
        }
    }
}
