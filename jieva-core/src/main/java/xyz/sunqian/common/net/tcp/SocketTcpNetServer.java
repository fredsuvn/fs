package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.net.NetException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

final class SocketTcpNetServer implements TcpNetServer {

    private final @Nonnull ServerSocket server;
    private final @Nonnull TcpNetListener listener;
    private final int bufSize;
    private final @Nonnull Executor acceptExecutor;
    private final @Nonnull Executor workExecutor;

    private volatile boolean doClose = false;
    private volatile CountDownLatch latch;

    SocketTcpNetServer(
        @Nonnull ServerSocket server,
        @Nonnull TcpNetListener listener,
        int bufSize,
        @Nonnull Executor acceptExecutor,
        @Nonnull Executor workExecutor
    ) {
        this.server = server;
        this.listener = listener;
        this.bufSize = bufSize;
        this.acceptExecutor = acceptExecutor;
        this.workExecutor = workExecutor;
    }

    @Override
    public void start() throws NetException {
        try {
            listener.onOpen();
            this.latch = new CountDownLatch(1);
        } catch (Exception e) {
            listener.onException(this, null, e);
        }
        acceptExecutor.execute(() -> {
            doAccept(server);
            latch.countDown();
        });
    }

    private void doAccept(@Nonnull ServerSocket server) {
        while (true) {
            try {
                Socket client = server.accept();
                workExecutor.execute(() -> doConnection(client));
            } catch (Exception e) {
                listener.onException(this, null, e);
                break;
            }
        }
        // must close the server
        synchronized (this) {
            if (!doClose) {
                try {
                    server.close();
                } catch (IOException e) {
                    listener.onException(this, null, e);
                }
                try {
                    listener.onClose();
                } catch (Exception e) {
                    listener.onException(this, null, e);
                }
                doClose = true;
            }
        }
    }

    private void doConnection(@Nonnull Socket client) {
        SocketEndpoint endpoint = new SocketEndpoint(client);
        try {
            listener.onConnection(endpoint);
            byte[] buf = new byte[bufSize];
            InputStream in = client.getInputStream();
            while (true) {
                int readSize = in.read(buf);
                if (readSize < 0) {
                    break;
                }
                byte[] msg = Arrays.copyOf(buf, readSize);
                try {
                    listener.onMessage(endpoint, ByteBuffer.wrap(msg));
                } catch (Exception e) {
                    listener.onException(this, endpoint, e);
                }
            }
        } catch (Exception e) {
            listener.onException(this, endpoint, e);
        }
        // onDisconnection
        try {
            listener.onDisconnection(endpoint);
        } catch (Exception e) {
            listener.onException(this, endpoint, e);
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
    public @Nullable InetSocketAddress getBoundAddress() {
        return null;
    }

    private static final class SocketEndpoint implements TcpNetEndpoint {

        private final @Nonnull Socket socket;
        private final @Nonnull InetSocketAddress address;

        private SocketEndpoint(@Nonnull Socket socket) {
            this.socket = socket;
            this.address = (InetSocketAddress) socket.getRemoteSocketAddress();
        }

        @Override
        public void close() throws NetException {
            try {
                socket.close();
            } catch (IOException e) {
                throw new NetException(e);
            }
        }

        @Override
        public @Nonnull InetSocketAddress getAddress() {
            return address;
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
}
