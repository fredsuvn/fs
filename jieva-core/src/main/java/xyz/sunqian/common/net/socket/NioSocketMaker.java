package xyz.sunqian.common.net.socket;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.io.communicate.IOChannelHandler;
import xyz.sunqian.common.net.NetException;

import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * The NIO implementation for {@link SocketMaker}.
 *
 * @author sunqian
 */
public class NioSocketMaker implements SocketMaker {

    @Override
    public @Nonnull TcpServerSpec makeTcpServer(
        @Nonnull IOChannelHandler<TcpChannelContext> handler, int handlerThreadNum
    ) throws NetException {
        return null;
    }


    private static final class TcpServerImpl implements TcpServer {


        @Override
        public void start() throws NetException {

        }

        @Override
        public void close() throws NetException {

        }

        @Override
        public void await() throws NetException {

        }

        @Override
        public @Nullable InetSocketAddress getBoundAddress() {
            return null;
        }

        private void doStart(
            @Nullable InetSocketAddress localAddress,
            int backlog,
            @Nonnull Map<SocketOption<Object>, Object> options
        ) throws Exception {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.bind(localAddress, backlog);
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            options.forEach((o, v) -> {
                try {
                    serverSocket.setOption(o, v);
                } catch (Exception e) {
                    throw new NetException(e);
                }
            });
            // while (true) {
            //     if (selector.select() == 0) continue;
            //     Set<SelectionKey> selectedKeys = selector.selectedKeys();
            //     Iterator<SelectionKey> iter = selectedKeys.iterator();
            //
            //     while (iter.hasNext()) {
            //         SelectionKey key = iter.next();
            //         iter.remove();  // 必须移除已处理的key
            //
            //         try {
            //             if (key.isAcceptable()) {
            //                 handleAccept(serverSocket, selector);
            //             }
            //
            //             if (key.isReadable()) {
            //                 handleRead(key);
            //             }
            //         } catch (IOException e) {
            //             key.cancel();
            //             key.channel().close();
            //             System.err.println("Client disconnected with error: " + e.getMessage());
            //         }
            //     }
            // }
        }
    }
}
