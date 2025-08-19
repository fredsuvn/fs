// package xyz.sunqian.common.net.socket;
//
// import xyz.sunqian.annotations.Nonnull;
// import xyz.sunqian.annotations.Nullable;
// import xyz.sunqian.common.io.BufferKit;
// import xyz.sunqian.common.net.NetChannelContext;
// import xyz.sunqian.common.net.NetChannelHandler;
// import xyz.sunqian.common.net.NetChannelHandlerWrapper;
// import xyz.sunqian.common.net.NetChannelType;
// import xyz.sunqian.common.net.NetException;
//
// import java.io.IOException;
// import java.net.InetSocketAddress;
// import java.net.SocketOption;
// import java.nio.ByteBuffer;
// import java.nio.channels.SelectionKey;
// import java.nio.channels.Selector;
// import java.nio.channels.ServerSocketChannel;
// import java.nio.channels.SocketChannel;
// import java.nio.channels.WritableByteChannel;
// import java.util.HashSet;
// import java.util.Iterator;
// import java.util.Map;
// import java.util.Set;
//
// /**
//  * The NIO implementation for {@link SocketMaker}.
//  *
//  * @author sunqian
//  */
// public class NioSocketMaker implements SocketMaker {
//
//     @Override
//     public @Nonnull TcpServerSpec makeTcpServer(
//         @Nonnull NetChannelHandler handler, int handlerThreadNum
//     ) throws NetException {
//         return null;
//     }
//
//
//     private static final class TcpServerImpl implements TcpServer {
//
//         private final @Nullable InetSocketAddress localAddress;
//         private final int backlog;
//         private final int bufSize;
//         private final @Nonnull NetChannelHandlerWrapper handler;
//         private final @Nonnull Map<@Nonnull SocketOption<@Nonnull Object>, Object> options;
//
//         private @Nullable ServerSocketChannel serverChannel;
//
//         private TcpServerImpl(
//             @Nullable InetSocketAddress localAddress,
//             int backlog,
//             int bufSize,
//             @Nonnull NetChannelHandlerWrapper handler,
//             @Nonnull Map<@Nonnull SocketOption<@Nonnull Object>, Object> options
//         ) {
//             this.localAddress = localAddress;
//             this.backlog = backlog;
//             this.bufSize = bufSize;
//             this.handler = handler;
//             this.options = options;
//         }
//
//         @Override
//         public void start() throws NetException {
//
//         }
//
//         @Override
//         public void close() throws NetException {
//
//         }
//
//         @Override
//         public void await() throws NetException {
//
//         }
//
//         @Override
//         public @Nullable InetSocketAddress getBoundAddress() {
//             return null;
//         }
//
//         private void doStart() throws Exception {
//             Selector selector = Selector.open();
//             ServerSocketChannel server = ServerSocketChannel.open();
//             this.serverChannel = server;
//             server.bind(localAddress, backlog);
//             server.configureBlocking(false);
//             server.register(selector, SelectionKey.OP_ACCEPT);
//             options.forEach((o, v) -> {
//                 try {
//                     server.setOption(o, v);
//                 } catch (Exception e) {
//                     throw new NetException(e);
//                 }
//             });
//             while (true) {
//                 selector.select();
//                 Set<SelectionKey> selectedKeys = selector.selectedKeys();
//                 Iterator<SelectionKey> keys = selectedKeys.iterator();
//                 while (keys.hasNext()) {
//                     SelectionKey key = keys.next();
//                     keys.remove();
//                     SocketChannel client = (SocketChannel) key.channel();
//                     TcpServerContext context = new TcpServerContext(server, client);
//                     // try {
//                     //     if (key.isAcceptable()) {
//                     //         handleAccept(serverSocket, selector);
//                     //     }
//                     //
//                     //     if (key.isReadable()) {
//                     //         handleRead(key);
//                     //     }
//                     // } catch (IOException e) {
//                     //     key.cancel();
//                     //     key.channel().close();
//                     //     System.err.println("Client disconnected with error: " + e.getMessage());
//                     // }
//                 }
//             }
//         }
//
//         private final class WorkThread extends Thread implements Runnable {
//
//             private final @Nonnull ServerSocketChannel serverSocket;
//             private final @Nonnull Selector selector;
//             private final @Nonnull Set<SocketChannel> clientSet = new HashSet<>();
//
//             {
//                 try {
//                     selector = Selector.open();
//                 } catch (IOException e) {
//                     throw new RuntimeException(e);
//                 }
//             }
//
//             private volatile @Nullable ClientNode head;
//             private volatile int clientNum;
//
//             private WorkThread(@Nonnull ServerSocketChannel serverSocket) {
//                 this.serverSocket = serverSocket;
//             }
//
//             @Override
//             public void run() {
//             }
//
//             private void doWork() throws Exception {
//                 // register client
//                 ClientNode clientNode = this.head;
//                 while (clientNode != null) {
//                     if (clientNode.done) {
//                         clientNode = clientNode.next;
//                         continue;
//                     }
//                     SocketChannel client = clientNode.client;
//                     registerClient(client);
//                     clientNode.done = true;
//                 }
//
//                 // handle
//                 doHandler();
//
//                 // remove closed client
//                 Set<SocketChannel> rm = new HashSet<>();
//                 for (SocketChannel channel : clientSet) {
//                     if (!channel.isOpen()) {
//                         rm.add(channel);
//                     }
//                 }
//                 clientSet.removeAll(rm);
//             }
//
//             private void registerClient(SocketChannel client) throws Exception {
//                 client.configureBlocking(false);
//                 ClientAttachment attachment = new ClientAttachment(ByteBuffer.allocate(bufSize));
//                 client.register(selector, SelectionKey.OP_READ, attachment);
//             }
//
//             private void doHandler() throws Exception {
//                 try {
//                     selector.select();
//                 } catch (IOException ignored) {
//                 }
//                 Set<SelectionKey> selectedKeys = selector.selectedKeys();
//                 Iterator<SelectionKey> keys = selectedKeys.iterator();
//                 while (keys.hasNext()) {
//                     SelectionKey key = keys.next();
//                     keys.remove();
//                     handleRead(key);
//                 }
//             }
//
//             private void handleRead(
//                 @Nonnull ServerSocketChannel socketChannel, @Nonnull SelectionKey key
//             ) throws Exception {
//                 SocketChannel client = (SocketChannel) key.channel();
//                 ClientAttachment attachment = (ClientAttachment) key.attachment();
//                 TcpServerContext context = attachment.context;
//                 if (context == null) {
//                     context = new TcpServerContext(socketChannel, client);
//                     attachment.context = context;
//                 }
//                 ByteBuffer buffer = attachment.buffer;
//
//                 // handle data
//                 while (true) {
//                     buffer.clear();
//                     int readNum = client.read(buffer);
//                     if (readNum < 0) {
//                         // channel closed
//                         handler.channelClose(context);
//                         break;
//                     }
//                     if (readNum == 0) {
//                         break;
//                     }
//                     buffer.flip();
//                     handler.channelRead(context, buffer);
//                 }
//             }
//         }
//     }
//
//     private static final class ClientNode {
//
//         private final @Nonnull ClientPack clientPack;
//
//         private volatile @Nullable ClientNode next;
//         private volatile boolean done = false;
//
//         private ClientNode(SocketChannel client) {
//             this.client = client;
//         }
//     }
//
//     private static final class ClientAttachment {
//
//         private final @Nonnull ByteBuffer buffer;
//         private @Nullable TcpServerContext context;
//
//         private ClientAttachment(@Nonnull ByteBuffer buffer) {
//             this.buffer = buffer;
//         }
//     }
//
//     private static final class TcpServerContext implements NetChannelContext {
//
//         private final @Nonnull ServerSocketChannel server;
//         private final @Nonnull SocketChannel client;
//
//         private TcpServerContext(@Nonnull ServerSocketChannel server, @Nonnull SocketChannel client) {
//             this.server = server;
//             this.client = client;
//         }
//
//         @Override
//         public NetChannelType channelType() {
//             return NetChannelType.tcpIp();
//         }
//
//         @Override
//         public InetSocketAddress getRemoteAddress() throws Exception {
//             return (InetSocketAddress) client.getRemoteAddress();
//         }
//
//         @Override
//         public InetSocketAddress getLocalAddress() throws Exception {
//             return (InetSocketAddress) server.getLocalAddress();
//         }
//
//         @Override
//         public void write(ByteBuffer buffer) {
//             BufferKit.readTo(buffer, client);
//         }
//
//         @Override
//         public WritableByteChannel asWritableByteChannel() {
//             return client;
//         }
//
//         @Override
//         public void close() throws IOException {
//             client.shutdownInput();
//             client.shutdownOutput();
//             client.finishConnect();
//             client.close();
//         }
//     }
// }
