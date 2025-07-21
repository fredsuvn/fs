package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.bytes.BytesKit;
import xyz.sunqian.common.base.value.BooleanVar;
import xyz.sunqian.common.collect.ArrayKit;
import xyz.sunqian.common.io.ByteReader;
import xyz.sunqian.common.io.ByteSegment;
import xyz.sunqian.common.net.NetException;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.function.IntFunction;

final class TcpSocketBack {

    static @Nonnull TcpNetServer newServer(
        int port,
        int backlog,
        @Nullable InetAddress bindAddr,
        @Nonnull TcpNetListener listener,
        @Nonnull Executor executor,
        int bufSize
    ) {
        return new SimpleTcpNetServer(port, backlog, bindAddr, listener, executor, bufSize);
    }

    static @Nonnull TcpNetServer newServer(
        @Nonnull ServerSocket serverSocket,
        @Nonnull TcpNetListener listener,
        @Nonnull Executor executor,
        int bufSize
    ) {
        return new SocketTcpNetServer(serverSocket, listener, executor, bufSize);
    }

    static ByteBuffer compact(ByteBuffer buffer, IntFunction<ByteBuffer> generator) {
        if (buffer.position() == 0) {
            return buffer;
        }
        if (buffer.remaining() <= 0) {
            return BytesKit.emptyBuffer();
        }
        ByteBuffer newBuffer = generator.apply(buffer.remaining());
        newBuffer.put(buffer);
        newBuffer.flip();
        return newBuffer.asReadOnlyBuffer();
    }

    static ByteBuffer compact(ByteBuffer buffer, byte[] newBytes, IntFunction<ByteBuffer> generator) {
        if (buffer.remaining() <= 0) {
            if (ArrayKit.isEmpty(newBytes)) {
                return BytesKit.emptyBuffer();
            }
            ByteBuffer newBuffer = generator.apply(newBytes.length);
            newBuffer.put(newBytes);
            newBuffer.flip();
            return newBuffer.asReadOnlyBuffer();
        }
        int newCapacity = buffer.remaining() + newBytes.length;
        ByteBuffer newBuffer = generator.apply(newCapacity);
        newBuffer.put(buffer);
        newBuffer.put(newBytes);
        newBuffer.flip();
        return newBuffer.asReadOnlyBuffer();
    }

    private static abstract class AbsTcpNetServer implements TcpNetServer {

        private final @Nonnull TcpNetListener listener;
        private final @Nonnull Executor executor;
        private final int bufSize;

        protected AbsTcpNetServer(@Nonnull TcpNetListener listener, @Nonnull Executor executor, int bufSize) {
            this.listener = listener;
            this.executor = executor;
            this.bufSize = bufSize;
        }

        protected abstract @Nonnull ServerSocket getServerSocket() throws Exception;

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
            doAccept(server);
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
    }

    private static final class SimpleTcpNetServer extends AbsTcpNetServer {

        private final int port;
        private final int backlog;
        private final @Nullable InetAddress bindAddr;

        SimpleTcpNetServer(
            int port,
            int backlog,
            @Nullable InetAddress bindAddr,
            @Nonnull TcpNetListener listener,
            @Nonnull Executor executor,
            int bufSize
        ) {
            super(listener, executor, bufSize);
            this.port = port;
            this.backlog = backlog;
            this.bindAddr = bindAddr;
        }

        protected @Nonnull ServerSocket getServerSocket() throws Exception {
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
    }

    private static final class SocketTcpNetServer extends AbsTcpNetServer {

        private final @Nonnull ServerSocket serverSocket;

        SocketTcpNetServer(
            @Nonnull ServerSocket serverSocket,
            @Nonnull TcpNetListener listener,
            @Nonnull Executor executor,
            int bufSize
        ) {
            super(listener, executor, bufSize);
            this.serverSocket = serverSocket;
        }

        protected @Nonnull ServerSocket getServerSocket() throws Exception {
            return serverSocket;
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
