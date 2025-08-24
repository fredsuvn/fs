package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.CheckKit;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.ListKit;
import xyz.sunqian.common.function.callable.VoidCallable;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.communicate.IOChannel;
import xyz.sunqian.common.net.NetChannelContext;
import xyz.sunqian.common.net.NetChannelHandler;
import xyz.sunqian.common.net.NetChannelHandlerWrapper;
import xyz.sunqian.common.net.NetChannelType;
import xyz.sunqian.common.net.NetException;
import xyz.sunqian.common.net.NetServer;

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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

/**
 * Builder for building new instances of {@link TcpServer} by Socket.
 *
 * @author sunqian
 */
public class TcpServerBuilder {

    private @Nullable InetSocketAddress localAddress;
    private @Nullable NetChannelHandlerWrapper handler;
    private int workerThreadNum = 1;
    private @Nullable ThreadFactory mainThreadFactory;
    private @Nullable ThreadFactory workerThreadFactory;
    private int backlog = -1;
    private int bufSize = IOKit.bufferSize();
    private final @Nonnull Map<SocketOption<?>, Object> socketOptions = new LinkedHashMap<>();

    /**
     * Sets the local address the server is bound to. If the local address is not configured, the server will auto bind
     * to an available address when it starts.
     *
     * @param localAddress the local address the server is bound to
     * @return this builder
     */
    public @Nonnull TcpServerBuilder localAddress(@Nonnull InetSocketAddress localAddress) {
        this.localAddress = localAddress;
        return this;
    }

    /**
     * Sets the handler to handle server events.
     *
     * @param handler the handler to handle server events
     * @return this builder
     */
    public @Nonnull TcpServerBuilder handler(@Nonnull NetChannelHandler handler) {
        this.handler = new NetChannelHandlerWrapper(handler);
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
    public @Nonnull TcpServerBuilder mainThreadFactory(@Nonnull ThreadFactory mainThreadFactory) {
        this.mainThreadFactory = mainThreadFactory;
        return this;
    }

    /**
     * Sets the worker thread factory to create worker thread. The main thread is responsible for accepting new client,
     * and then the worker thread will take over the already connected clients.
     * <p>
     * If the factory is not configured, the server will use {@link Thread#Thread(Runnable)}.
     *
     * @param workerThreadFactory the worker thread factory
     * @return this builder
     */
    public @Nonnull TcpServerBuilder workerThreadFactory(@Nonnull ThreadFactory workerThreadFactory) {
        this.workerThreadFactory = workerThreadFactory;
        return this;
    }

    /**
     * Sets the number of worker thread. The default is {@code 1}.
     *
     * @param workThreadNum the number of worker thread, must {@code >= 1}
     * @return this builder
     * @throws IllegalArgumentException if the number is negative or {@code 0}
     * @see #workerThreadFactory(ThreadFactory)
     */
    public @Nonnull TcpServerBuilder workerThreadNum(int workThreadNum) throws IllegalArgumentException {
        CheckKit.checkArgument(workThreadNum >= 1, "workThreadNum must >= 1");
        this.workerThreadNum = workThreadNum;
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
    public @Nonnull TcpServerBuilder backlog(int backlog) {
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
    public @Nonnull TcpServerBuilder bufferSize(int bufSize) throws IllegalArgumentException {
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
    public <T> @Nonnull TcpServerBuilder socketOption(@Nonnull SocketOption<T> name, T value) throws NetException {
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
        return Jie.uncheck(() -> new TcpServerImpl(
                localAddress,
                handler,
                mainThreadFactory,
                workerThreadFactory,
                workerThreadNum,
                socketOptions,
                backlog,
                bufSize
            ),
            NetException::new
        );
    }

    private static final class TcpServerImpl implements TcpServer, Runnable {

        private final @Nonnull ServerSocketChannel server;
        private final @Nonnull Selector mainSelector;
        private final @Nonnull Thread mainThread;
        private final @Nonnull TcpWorker @Nonnull [] workers;
        private final @Nonnull NetChannelHandlerWrapper handler;
        private final @Nullable InetSocketAddress localAddress;
        private final int backlog;
        private final int bufSize;

        // 0: not started, 1: started, 2: closed
        private volatile int state = 0;

        @SuppressWarnings("resource")
        private TcpServerImpl(
            @Nullable InetSocketAddress localAddress,
            @Nonnull NetChannelHandlerWrapper handler,
            @Nullable ThreadFactory mainthreadFactory,
            @Nullable ThreadFactory workerthreadFactory,
            int workThreadNum,
            Map<SocketOption<?>, Object> socketOptions,
            int backlog,
            int bufSize
        ) throws Exception {
            this.server = ServerSocketChannel.open();
            this.mainSelector = Selector.open();
            this.localAddress = localAddress;
            this.handler = handler;
            this.mainThread = newThread(mainthreadFactory, this);
            this.workers = new TcpWorker[workThreadNum];
            server.configureBlocking(false);
            socketOptions.forEach((name, value) ->
                Jie.uncheck(() -> server.setOption(Jie.as(name), value), NetException::new));
            this.bufSize = bufSize;
            this.backlog = backlog;
            server.register(mainSelector, SelectionKey.OP_ACCEPT);
            for (int i = 0; i < workThreadNum; i++) {
                TcpWorker worker = new TcpWorker();
                workers[i] = worker;
                worker.thread = newThread(workerthreadFactory, worker);
            }
        }

        private @Nonnull Thread newThread(@Nullable ThreadFactory factory, @Nonnull Runnable runnable) {
            return factory == null ? new Thread(runnable) : factory.newThread(runnable);
        }

        @SuppressWarnings("resource")
        @Override
        public synchronized void start() throws NetException {
            if (state == 1) {
                throw new NetException("This server has already started.");
            }
            if (state == 2) {
                throw new NetException("This server has already closed.");
            }
            state = 1;
            Jie.uncheck(() -> server.bind(localAddress, backlog), NetException::new);
            mainThread.start();
        }

        @Override
        public synchronized void close() throws NetException {
            if (state != 1) {
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
            releaseWorkers();
            state = 2;
        }

        @Override
        public void await() throws NetException {
            try {
                mainThread.join();
                for (TcpWorker worker : workers) {
                    worker.thread.join();
                }
            } catch (InterruptedException ignored) {
            }
        }

        @Override
        public @Nonnull InetSocketAddress localAddress() throws NetException {
            return (InetSocketAddress) Jie.uncheck(server::getLocalAddress, NetException::new);
        }

        @Override
        public List<NetServer.Worker> workers() {
            return ListKit.list(workers);
        }

        @Override
        public boolean isStarted() {
            return state == 1;
        }

        @Override
        public boolean isClosed() {
            return isClosed(state);
        }

        private boolean isClosed(int state) {
            return state == 2;
        }

        @Override
        public void run() {
            for (TcpWorker worker : workers) {
                worker.thread.start();
            }
            while (!mainThread.isInterrupted()) {
                doWork(this::doMainWork, state);
            }
            releaseWorkers();
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
                handleAccept(key, workers);
            }
        }

        @SuppressWarnings("resource")
        private void handleAccept(SelectionKey key, TcpWorker[] workers) throws Exception {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            TcpContext context = new TcpContext(client);
            int index = findWorker(workers);
            workers[index].registerClient(context);
            workers[index].selector.wakeup();
        }

        private int findWorker(TcpWorker[] workers) {
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

        private void releaseWorkers() {
            for (TcpWorker worker : workers) {
                worker.thread.interrupt();
            }
            for (TcpWorker worker : workers) {
                try {
                    worker.thread.join();
                } catch (InterruptedException ignored) {
                }
            }
        }

        private void doWork(VoidCallable callable, int state) {
            if (isClosed(state)) {
                return;
            }
            try {
                callable.call();
            } catch (Exception e) {
                handler.exceptionCaught(null, e);
            }
        }

        private final class TcpWorker implements Worker, Runnable {

            private final @Nonnull Selector selector;
            private final @Nonnull Set<TcpContext> clientSet = new HashSet<>();

            // the thread this worker starts on
            private Thread thread;

            private volatile @Nullable ClientNode clientNode;

            private TcpWorker() {
                this.selector = Jie.uncheck(Selector::open, NetException::new);
            }

            //@SuppressWarnings({"InfiniteLoopStatement"})
            @Override
            public void run() {
                Thread thread = Thread.currentThread();
                while (!thread.isInterrupted()) {
                    doWork(this::doWorkerWork, state);
                }
                releaseClients();
                Jie.uncheck(selector::close, NetException::new);
            }

            private void doWorkerWork() throws Exception {
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

            @Override
            public int clientCount() {
                return clientSet.size();
            }

            @Override
            public @Nonnull Thread thread() {
                return thread;
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
                try {
                    client.close();
                } catch (Exception e) {
                    // handler.exceptionCaught(this, e);
                } finally {
                    handler.channelClose(this);
                }
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
