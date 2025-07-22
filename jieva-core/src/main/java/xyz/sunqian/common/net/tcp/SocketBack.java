package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.bytes.BytesKit;
import xyz.sunqian.common.base.value.BooleanVar;
import xyz.sunqian.common.collect.ArrayKit;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.net.NetException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.function.IntFunction;

final class SocketBack {

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

    private static final class SocketTcpNetServer implements TcpNetServer {

        private final @Nonnull ServerSocket server;
        private final @Nonnull TcpNetListener listener;
        private final @Nonnull Executor executor;
        private final int bufSize;

        private CountDownLatch latch;

        private SocketTcpNetServer(
            @Nonnull ServerSocket server,
            @Nonnull TcpNetListener listener,
            @Nonnull Executor executor,
            int bufSize
        ) {
            this.server = server;
            this.listener = listener;
            this.executor = executor;
            this.bufSize = bufSize;
        }

        @Override
        public void start() throws NetException {
            try {
                listener.onOpen();
                this.latch = new CountDownLatch(1);
            } catch (Exception e) {
                listener.onException(this, null, e);
            }
            executor.execute(() -> {
                doAccept(server);
                latch.countDown();
            });
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
                InputStream in = client.getInputStream();
                while (true) {
                    byte[] buf = new byte[bufSize];
                    int readSize = in.read(buf);
                    if (readSize < 0) {
                        listener.onDisconnection(endpoint, !endpoint.isClosed.get());
                        break;
                    }
                    ByteBuffer msg = ByteBuffer.wrap(buf, 0, readSize);
                    listener.onMessage(endpoint, msg);
                }
            } catch (Exception e) {
                listener.onException(this, endpoint, e);
                try {
                    listener.onDisconnection(endpoint, !endpoint.isClosed.get());
                } catch (Exception ex) {
                    listener.onException(this, endpoint, e);
                }
            }
        }

        @Override
        public void close() throws NetException {
            try {
                server.close();
            } catch (Exception e) {
                throw new NetException(e);
            }
        }

        @Override
        public void await() throws NetException {
            try {
                latch.await();
            } catch (InterruptedException ignored) {
            }
        }

        @Override
        public int getPort() {
            return server.getLocalPort();
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
            return socket.getInetAddress();
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
            // socket.close();
        }

        @Override
        public void send(ByteBuffer msg) {
            try {
                OutputStream out = socket.getOutputStream();
                out.write(Objects.requireNonNull(BufferKit.read(msg)));
                out.flush();
            } catch (Exception ignored) {
            }
        }
    }
}
