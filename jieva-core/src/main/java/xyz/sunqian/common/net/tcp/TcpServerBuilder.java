package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.CheckKit;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.ListKit;
import xyz.sunqian.common.function.callable.VoidCallable;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.communicate.AbstractChannelContext;
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
 * Builder for building new instances of {@link TcpServer} by {@link ServerSocketChannel} and {@link SocketChannel}.
 * <p>
 * The server built by this builder requires a main thread and at least one worker thread, the main thread is
 * responsible for accepting new client, and the worker threads are responsible for handling connected client. A client
 * is always handled by one worker thread, so there is no client thread safety issues in the {@link TcpServerHandler}.
 *
 * @author sunqian
 */
public class TcpServerBuilder {

    private @Nonnull TcpServerHandler handler = TcpServerHandler.nullHandler();
    private int workerThreadNum = 1;
    private @Nullable ThreadFactory mainThreadFactory;
    private @Nullable ThreadFactory workerThreadFactory;
    private int bufSize = IOKit.bufferSize();
    private final @Nonnull Map<SocketOption<?>, Object> socketOptions = new LinkedHashMap<>();

    /**
     * Sets the handler to handle server events. The default handler is {@link TcpServerHandler#nullHandler()}.
     *
     * @param handler the handler to handle server events
     * @return this builder
     */
    public @Nonnull TcpServerBuilder handler(@Nonnull TcpServerHandler handler) {
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
     * Binds the server's socket to the automatically assigned address and configures the socket to listen for
     * connections. And a new {@link TcpServer} instance is returned.
     *
     * @return a new {@link TcpServer} instance
     * @throws NetException If an error occurs
     */
    public @Nonnull TcpServer bind() throws NetException {
        return bind(null);
    }

    /**
     * Binds the server's socket to the specified local address and configures the socket to listen for connections. And
     * a new {@link TcpServer} instance is returned.
     *
     * @param localAddress the local address the server is bound to, may be {@code null} to bind to the automatically
     *                     assigned address
     * @return a new {@link TcpServer} instance
     * @throws NetException If an error occurs
     */
    public @Nonnull TcpServer bind(@Nullable InetSocketAddress localAddress) throws NetException {
        return bind(localAddress, 0);
    }

    /**
     * Binds the server's socket to the specified local address and configures the socket to listen for connections. And
     * a new {@link TcpServer} instance is returned.
     * <p>
     * The {@code backlog} is the maximum number of pending connections on the socket. If the {@code backlog} parameter
     * has the value 0, or a negative value, then a default value is used.
     *
     * @param localAddress the local address the server is bound to, may be {@code null} to bind to the automatically
     *                     assigned address
     * @param backlog      the maximum number of pending connections
     * @return a new {@link TcpServer} instance
     * @throws NetException If an error occurs
     */
    public @Nonnull TcpServer bind(@Nullable InetSocketAddress localAddress, int backlog) throws NetException {
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
        private final @Nonnull WorkerImpl @Nonnull [] workers;
        private final @Nonnull TcpServerHandler handler;
        private final @Nonnull InetSocketAddress localAddress;
        private final int bufSize;

        private volatile boolean closed = false;

        @SuppressWarnings("resource")
        private TcpServerImpl(
            @Nullable InetSocketAddress localAddress,
            @Nonnull TcpServerHandler handler,
            @Nullable ThreadFactory mainthreadFactory,
            @Nullable ThreadFactory workerthreadFactory,
            int workThreadNum,
            Map<SocketOption<?>, Object> socketOptions,
            int backlog,
            int bufSize
        ) throws Exception {
            this.server = ServerSocketChannel.open();
            this.mainSelector = Selector.open();
            this.handler = handler;
            this.mainThread = newThread(mainthreadFactory, this);
            this.workers = new WorkerImpl[workThreadNum];
            server.configureBlocking(false);
            socketOptions.forEach((name, value) ->
                Jie.uncheck(() -> server.setOption(Jie.as(name), value), NetException::new));
            server.register(mainSelector, SelectionKey.OP_ACCEPT);
            for (int i = 0; i < workThreadNum; i++) {
                WorkerImpl worker = new WorkerImpl();
                workers[i] = worker;
                worker.thread = newThread(workerthreadFactory, worker);
            }
            server.bind(localAddress, backlog);
            this.localAddress = (InetSocketAddress) server.getLocalAddress();
            this.bufSize = bufSize;
            mainThread.start();
        }

        private @Nonnull Thread newThread(@Nullable ThreadFactory factory, @Nonnull Runnable runnable) {
            return factory == null ? new Thread(runnable) : factory.newThread(runnable);
        }

        @Override
        public void await() throws NetException {
            try {
                mainThread.join();
                for (WorkerImpl worker : workers) {
                    worker.thread.join();
                }
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
            releaseWorkers();
            closed = true;
        }

        @Override
        public @Nonnull InetSocketAddress localAddress() throws NetException {
            return localAddress;
        }

        @Override
        public @Nonnull List<NetServer.@Nonnull Worker> workers() {
            return ListKit.list(workers);
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public void run() {
            for (WorkerImpl worker : workers) {
                worker.thread.start();
            }
            while (!mainThread.isInterrupted()) {
                doWork(this::doMainWork, closed);
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
                // key.cancel();
            }
        }

        @SuppressWarnings("resource")
        private void handleAccept(SelectionKey key, WorkerImpl[] workers) throws Exception {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            int index = findWorker(workers);
            workers[index].registerClient(client);
            workers[index].selector.wakeup();
        }

        private int findWorker(WorkerImpl[] workers) {
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
            for (WorkerImpl worker : workers) {
                worker.thread.interrupt();
            }
            for (WorkerImpl worker : workers) {
                try {
                    worker.thread.join();
                } catch (InterruptedException ignored) {
                }
            }
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

        private final class WorkerImpl implements Worker, Runnable {

            private final @Nonnull Selector selector;
            private final @Nonnull Set<TcpContext> clientSet = new HashSet<>();

            // the thread this worker starts on
            private Thread thread;

            private volatile @Nullable ClientNode clientNode;

            private WorkerImpl() {
                this.selector = Jie.uncheck(Selector::open, NetException::new);
            }

            //@SuppressWarnings({"InfiniteLoopStatement"})
            @Override
            public void run() {
                Thread thread = Thread.currentThread();
                while (!thread.isInterrupted()) {
                    doWork(this::doWorkerWork, closed);
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
                        SocketChannel channel = node.channel;
                        TcpContext context = new TcpContext(channel, bufSize);
                        clientSet.add(context);
                        registerRead(context);
                        TcpKit.channelOpen(handler, context);
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

            @SuppressWarnings("resource")
            private void registerRead(TcpContext context) throws Exception {
                SocketChannel channel = context.channel();
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_READ, context);
            }

            private void handleRead() throws Exception {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keys = selectedKeys.iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    TcpKit.channelRead(handler, (TcpContext) key.attachment());
                }
            }

            @SuppressWarnings("resource")
            private void handleClose() {
                Set<TcpContext> rm = new HashSet<>();
                for (TcpContext context : clientSet) {
                    if (!context.channel().isOpen()) {
                        rm.add(context);
                        context.close();
                    }
                }
                clientSet.removeAll(rm);
            }

            public void registerClient(SocketChannel client) {
                ClientNode node = new ClientNode(client);
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

            private final class TcpContext
                extends AbstractChannelContext<SocketChannel> implements TcpServerHandler.Context {

                private final @Nonnull InetSocketAddress clientAddress;
                private final @Nonnull InetSocketAddress serverAddress;

                private volatile boolean closed = false;

                private TcpContext(@Nonnull SocketChannel channel, int bufSize) throws IllegalArgumentException {
                    super(channel, bufSize);
                    this.clientAddress = (InetSocketAddress) Jie.uncheck(channel::getLocalAddress, NetException::new);
                    this.serverAddress = (InetSocketAddress) Jie.uncheck(channel::getRemoteAddress, NetException::new);
                }

                @Override
                public @Nonnull InetSocketAddress clientAddress() {
                    return clientAddress;
                }

                @Override
                public @Nonnull InetSocketAddress serverAddress() {
                    return serverAddress;
                }

                @Override
                public synchronized void close() throws NetException {
                    if (closed) {
                        return;
                    }
                    Jie.uncheck(() -> {
                        channel.close();
                        channel.keyFor(selector).cancel();
                        TcpKit.channelClose(handler, this);
                    }, NetException::new);
                    closed = true;
                }
            }
        }

        private static final class ClientNode {

            private final @Nonnull SocketChannel channel;
            private volatile boolean done = false;
            private volatile @Nullable ClientNode next;

            private ClientNode(@Nonnull SocketChannel channel) {
                this.channel = channel;
            }
        }
    }
}
