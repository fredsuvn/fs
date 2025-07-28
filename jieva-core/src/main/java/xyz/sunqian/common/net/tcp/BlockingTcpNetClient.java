package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.math.MathKit;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.net.NetException;
import xyz.sunqian.common.net.NetOption;
import xyz.sunqian.common.net.NetState;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

final class BlockingTcpNetClient implements TcpNetClient {

    private final @Nonnull InetSocketAddress remoteAddress;
    private final @Nullable InetSocketAddress localAddress;
    private final @Nullable List<NetOption<?>> netOptions;
    private final @Nullable Duration connectTimeout;
    private final @Nonnull ClientListenerWrapper listener;
    private final int bufSize;
    private final @Nonnull Executor workExecutor;

    private volatile @Nullable Socket socket;
    private volatile @Nonnull NetState state = NetState.CREATED;
    private volatile @Nullable CountDownLatch latch;

    BlockingTcpNetClient(
        @Nonnull InetSocketAddress remoteAddress,
        @Nullable InetSocketAddress localAddress,
        @Nullable List<NetOption<?>> netOptions,
        @Nullable Duration connectTimeout,
        @Nonnull TcpNetClient.Listener listener,
        int bufSize,
        @Nonnull Executor workExecutor
    ) {
        this.remoteAddress = remoteAddress;
        this.localAddress = localAddress;
        this.netOptions = netOptions;
        this.connectTimeout = connectTimeout;
        this.listener = new ClientListenerWrapper(this, listener);
        this.bufSize = bufSize;
        this.workExecutor = workExecutor;
    }

    @Override
    public void start() throws NetException {
        synchronized (this) {
            if (socket != null) {
                throw new NetException("This client has already started.");
            }
            start0();
        }
    }

    private void start0() throws NetException {
        state = NetState.STARTUP;
        Socket client = new Socket();
        try {
            client.bind(localAddress);
            if (netOptions != null) {
                for (NetOption<?> netOption : netOptions) {
                    netOption.applyTo(client);
                }
            }
        } catch (Exception e) {
            state = NetState.START_FAILED;
            throw new NetException(e);
        }
        state = NetState.RUNNING;
        listener.onOpen();
        TcpNetEndpoint server = new ServerEndpoint(client);
        try {
            if (connectTimeout == null) {
                client.connect(remoteAddress);
            } else {
                client.connect(remoteAddress, MathKit.intValue(connectTimeout.toMillis()));
            }
            listener.onConnection(server);
        } catch (Exception e) {
            state = NetState.START_FAILED;
            listener.onConnectionFailed(server, e);
        }
        this.latch = new CountDownLatch(1);
        workExecutor.execute(() -> doConnection(client, server));
    }

    private void doConnection(@Nonnull Socket client, @Nonnull TcpNetEndpoint server) {
        try {
            byte[] buf = new byte[bufSize];
            InputStream in = client.getInputStream();
            while (true) {
                int readSize = in.read(buf);
                if (readSize < 0) {
                    break;
                }
                byte[] msg = Arrays.copyOf(buf, readSize);
                listener.onMessage(server, ByteBuffer.wrap(msg));
            }
        } catch (Exception e) {
            listener.onException(this, server, e);
        }
        // onDisconnection
        listener.onDisconnection(server);
        state = NetState.CLOSING;
        closeSocket(client);
        state = NetState.CLOSED;
        // onCLose
        listener.onClose();
    }

    @Override
    public void close() throws NetException {
        synchronized (this) {
            Socket client = this.socket;
            if (client != null) {
                state = NetState.CLOSING;
                closeSocket(client);
                state = NetState.CLOSED;
            }
        }
    }

    @Override
    public void await() throws NetException {
        CountDownLatch latch = this.latch;
        if (latch == null) {
            throw new NetException("This client has not been started yet.");
        }
        try {
            latch.await();
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public @Nullable InetSocketAddress getRemoteAddress() {
        Socket client = this.socket;
        if (client == null) {
            return null;
        }
        return (InetSocketAddress) client.getRemoteSocketAddress();
    }

    @Override
    public @Nullable InetSocketAddress getLocalAddress() {
        Socket client = this.socket;
        if (client == null) {
            return null;
        }
        return (InetSocketAddress) client.getLocalSocketAddress();
    }

    @Override
    public NetState getState() {
        return state;
    }

    private final class ServerEndpoint implements TcpNetEndpoint {

        private final Socket socket;

        private ServerEndpoint(@Nonnull Socket socket) {
            this.socket = socket;
        }

        @Override
        public void close() throws NetException {
            closeSocket(socket);
        }

        @Override
        public @Nonnull InetSocketAddress getAddress() {
            return remoteAddress;
        }

        @Override
        public void send(ByteBuffer msg) throws NetException {
            try {
                OutputStream out = socket.getOutputStream();
                BufferKit.readTo(msg, out);
                out.flush();
            } catch (Exception e) {
                throw new NetException(e);
            }
        }
    }

    private static void closeSocket(Socket socket) throws NetException {
        try {
            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();
        } catch (Exception e) {
            throw new NetException(e);
        }
    }
}
