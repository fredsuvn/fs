package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.value.BooleanVar;
import xyz.sunqian.common.io.ByteReader;
import xyz.sunqian.common.io.ByteSegment;
import xyz.sunqian.common.net.NetException;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.concurrent.Executor;

final class TcpNetServerImpl implements TcpNetServer {

    private final int port;
    private final int backlog;
    private final @Nullable InetAddress bindAddr;
    private final @Nonnull TcpNetListener listener;
    private final @Nonnull Executor executor;
    private final int bufSize;

    private @Nullable ServerSocket serverSocket;

    TcpNetServerImpl(
        int port,
        int backlog,
        @Nullable InetAddress bindAddr,
        @Nonnull TcpNetListener listener,
        @Nonnull Executor executor, int bufSize
    ) {
        this.port = port;
        this.backlog = backlog;
        this.bindAddr = bindAddr;
        this.listener = listener;
        this.executor = executor;
        this.bufSize = bufSize;
    }

    @Override
    public void start() throws NetException {
        ServerSocket server;
        try {
            server = getServerSocket();
        } catch (Exception e) {
            throw new NetException(e);
        }
        try {
            listener.onOpen();
        } catch (Exception e) {
            listener.onException(this, null, e);
        }
    }

    private @Nonnull ServerSocket getServerSocket() throws Exception {
        if (serverSocket != null) {
            return serverSocket;
        }
        ServerSocket newServerSocket;
        if (bindAddr == null) {
            if (backlog < 0) {
                newServerSocket = new ServerSocket(port);
            } else {
                newServerSocket = new ServerSocket(port, backlog);
            }
        } else {
            if (backlog < 0) {
                newServerSocket = new ServerSocket(port, 50, bindAddr);
            } else {
                newServerSocket = new ServerSocket(port, backlog, bindAddr);
            }
        }
        return newServerSocket;
    }

    private void doAccept(@Nonnull ServerSocket server) {
        boolean loop = true;
        while (loop) {
            try {
                Socket client = server.accept();
                executor.execute(() -> doConnection(client));
            } catch (Exception e) {
                if (server.isClosed()) {
                    try {
                        listener.onClose();
                    } catch (Exception ex) {
                        listener.onException(this, null, ex);
                    } finally {
                        loop = false;
                    }
                } else {
                    listener.onException(this, null, e);
                }
            }
        }
    }

    private void doConnection(@Nonnull Socket client) {
        TcpNetEndpointImpl endpoint = new TcpNetEndpointImpl(client);
        try {
            listener.onConnection(endpoint);
            ByteReader reader = ByteReader.from(client.getInputStream());
            while (true) {
                ByteSegment next = reader.read(bufSize);
                if (next.data().hasRemaining()) {
                    listener.onMessage(endpoint, next.data());
                }
                if (next.end()) {
                    break;
                }
            }
            listener.onDisconnection(endpoint, !endpoint.isClosed.get());
        } catch (Exception e) {
            listener.onException(this, endpoint, e);
        }
    }

    @Override
    public void close() throws NetException {
        try {
            ServerSocket serverSocket = getServerSocket();
            serverSocket.close();
        } catch (Exception e) {
            throw new NetException(e);
        }
    }

    private static final class TcpNetEndpointImpl implements TcpNetEndpoint {

        private final @Nonnull Socket socket;

        @Nonnull
        BooleanVar isClosed = BooleanVar.of(false);

        private TcpNetEndpointImpl(@Nonnull Socket socket) {
            this.socket = socket;
        }

        @Override
        public InetAddress getAddress() {
            return null;
        }

        @Override
        public int getPort() {
            return 0;
        }

        @Override
        public SocketAddress getSocketAddress() {
            return null;
        }

        @Override
        public Object getSource() {
            return null;
        }

        @Override
        public boolean isOpened() {
            return false;
        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public void close(@Nullable Duration timeout) {

        }

        @Override
        public void closeNow() {

        }
    }
}
