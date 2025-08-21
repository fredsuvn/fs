package xyz.sunqian.common.net.socket;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.net.NetChannelContext;
import xyz.sunqian.common.net.NetChannelHandlerWrapper;
import xyz.sunqian.common.net.NetChannelType;
import xyz.sunqian.common.net.NetException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

public class TcpServerBuilder {

    private final @Nonnull ServerSocketChannel server = Jie.uncheck(ServerSocketChannel::open, NetException::new);

    private @Nullable InetSocketAddress localAddress;
    private int backlog;
    private @Nonnull NetChannelHandlerWrapper handler;
    private @Nonnull ThreadFactory threadFactory;
    private int workThreadNum;

    /**
     * Sets the value of a socket option.
     *
     * @param <T>   the type of the socket option value
     * @param name  the socket option
     * @param value the value of the socket option, a value of {@code null} may be a valid value for some socket
     *              options.
     * @return this builder
     * @throws NetException If an error occurs
     * @see java.net.StandardSocketOptions
     */
    public <T> @Nonnull TcpServerBuilder socketOption(@Nonnull SocketOption<T> name, T value) throws NetException {
        try {
            server.setOption(name, value);
        } catch (Exception e) {
            throw new NetException(e);
        }
        return this;
    }

    @SuppressWarnings({"InfiniteLoopStatement"})
    public @Nonnull ServerSocketChannel start() throws Exception {
        Selector selector = Selector.open();
        server.bind(localAddress, backlog);
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);
        // Thread[] workThreads = new Thread[workThreadNum];
        Worker[] workers = new Worker[workThreadNum];
        for (int i = 0; i < workThreadNum; i++) {
            Worker worker = new Worker();
            workers[i] = worker;
            Thread workThread = new Thread(worker);
            // workThreads[i] = workThread;
            workThread.start();
        }
        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keys = selectedKeys.iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();
                handleAccept(key, workers);
            }
        }
    }

    private void handleAccept(SelectionKey key, Worker[] workers) {
        SocketChannel client = (SocketChannel) key.channel();
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

        private volatile @Nullable ClientNode clientNode;

        {
            try {
                selector = Selector.open();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

        @SuppressWarnings({"InfiniteLoopStatement"})
        @Override
        public void run() {
            try {
                while (true) {
                    doWork();
                }
            } catch (InterruptedException e) {
                releaseClients();
            } catch (Exception e) {
                handler.exceptionCaught(null, e);
            }
        }

        private void doWork() throws Exception {
            // register client
            ClientNode head = this.clientNode;
            if (head != null) {
                doNode(head);
            }
            // handle
            doSelect();
            // remove closed client
            doCheckClose();
        }

        private void doNode(@Nonnull ClientNode head) throws Exception {
            @Nonnull ClientNode node = head;
            while (true) {
                if (!node.done) {
                    TcpContext context = node.context;
                    clientSet.add(context);
                    registerRead(context);
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

        private void doSelect() throws Exception {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keys = selectedKeys.iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();
                doHandler(key);
            }
        }

        private void doHandler(@Nonnull SelectionKey key) {
            TcpContext context = (TcpContext) key.attachment();
            handler.channelRead(context, context.client);
        }

        private void doCheckClose() {
            Set<TcpContext> rm = new HashSet<>();
            for (TcpContext context : clientSet) {
                if (!context.client.isOpen()) {
                    rm.add(context);
                    context.handleClose();
                }
            }
            clientSet.removeAll(rm);
        }

        private void releaseClients() {
            for (TcpContext context : clientSet) {
                try {
                    context.close();
                    context.handleClose();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private final class TcpContext implements NetChannelContext {

        private final @Nonnull SocketChannel client;
        private boolean handleClose = false;

        private TcpContext(@Nonnull SocketChannel client) {
            this.client = client;
        }

        @Override
        public NetChannelType channelType() {
            return NetChannelType.tcpIp();
        }

        @Override
        public InetSocketAddress remoteAddress() throws Exception {
            return (InetSocketAddress) client.getRemoteAddress();
        }

        @Override
        public InetSocketAddress localAddress() throws Exception {
            return (InetSocketAddress) server.getLocalAddress();
        }

        @Override
        public void write(ByteBuffer buffer) {
            BufferKit.readTo(buffer, client);
        }

        @Override
        public WritableByteChannel writer() {
            return client;
        }

        @Override
        public void close() throws IOException {
            client.shutdownInput();
            client.shutdownOutput();
            client.finishConnect();
            client.close();
        }

        private void handleClose() {
            if (handleClose) {
                return;
            }
            handler.channelClose(this);
            handleClose = true;
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
