// package tests.net;
//
// import org.testng.annotations.Test;
// import xyz.sunqian.annotations.Nonnull;
// import xyz.sunqian.annotations.Nullable;
// import xyz.sunqian.common.base.bytes.BytesBuilder;
// import xyz.sunqian.common.base.exception.ThrowKit;
// import xyz.sunqian.common.base.thread.ThreadGate;
// import xyz.sunqian.common.base.value.IntVar;
// import xyz.sunqian.common.function.callable.VoidCallable;
// import xyz.sunqian.common.net.NetChannelContext;
// import xyz.sunqian.common.net.NetChannelHandler;
// import xyz.sunqian.common.net.NetChannelType;
// import xyz.sunqian.common.net.NetException;
// import xyz.sunqian.common.net.NetServer;
// import xyz.sunqian.common.net.tcp.TcpClient;
// import xyz.sunqian.common.net.tcp.TcpServer;
// import xyz.sunqian.test.DataTest;
// import xyz.sunqian.test.PrintTest;
//
// import java.lang.reflect.Method;
// import java.net.InetSocketAddress;
// import java.net.StandardSocketOptions;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.concurrent.CountDownLatch;
// import java.util.concurrent.ThreadFactory;
// import java.util.concurrent.atomic.AtomicInteger;
//
// import static org.testng.Assert.assertEquals;
// import static org.testng.Assert.assertFalse;
// import static org.testng.Assert.assertNotNull;
// import static org.testng.Assert.assertSame;
// import static org.testng.Assert.assertTrue;
// import static org.testng.Assert.expectThrows;
//
// public class TcpTest implements DataTest, PrintTest {
//
//     @Test
//     public void testTcp() throws Exception {
//         byte[] data = randomBytes(16);
//         int workerNum = 5;
//         AtomicInteger closeCount = new AtomicInteger();
//         CountDownLatch closeLatch = new CountDownLatch(workerNum);
//
//         class XThread extends Thread {
//
//             private int num;
//
//             public XThread(int num, Runnable target) {
//                 super(target);
//                 this.num = num;
//             }
//         }
//
//         IntVar workerCount = IntVar.of(0);
//         ThreadFactory workerFactory = r -> new XThread(workerCount.getAndIncrement(), () -> {
//             r.run();
//             closeCount.incrementAndGet();
//             closeLatch.countDown();
//         });
//
//         TcpClient[] clients = new TcpClient[workerNum];
//         BytesBuilder[] builders = new BytesBuilder[workerNum];
//         for (int i = 0; i < builders.length; i++) {
//             builders[i] = new BytesBuilder();
//         }
//
//         CountDownLatch[] openLatches = new CountDownLatch[workerNum];
//         for (int i = 0; i < openLatches.length; i++) {
//             openLatches[i] = new CountDownLatch(1);
//         }
//         CountDownLatch[] readLatches = new CountDownLatch[workerNum];
//         for (int i = 0; i < openLatches.length; i++) {
//             readLatches[i] = new CountDownLatch(1);
//         }
//
//         TcpServer server = TcpServer.newBuilder()
//             .workerThreadNum(workerNum)
//             .workerThreadFactory(workerFactory)
//             .socketOption(StandardSocketOptions.SO_RCVBUF, 1024)
//             .bufferSize(1024)
//             .handler(new NetChannelHandler() {
//
//                 @Override
//                 public void channelOpen(@Nonnull NetChannelContext context) {
//                     assertTrue(context.isOpen());
//                     assertSame(context.channelType(), NetChannelType.tcpIp());
//                     XThread thread = (XThread) Thread.currentThread();
//                     openLatches[thread.num].countDown();
//                     printFor("client open", thread.num);
//                     assertEquals(clients[thread.num].remoteAddress(), context.localAddress());
//                     assertEquals(clients[thread.num].localAddress(), context.remoteAddress());
//                 }
//
//                 @Override
//                 public void channelRead(@Nonnull NetChannelContext context) {
//                     XThread thread = (XThread) Thread.currentThread();
//                     printFor("client read", thread.num);
//                     assertEquals(clients[thread.num].remoteAddress(), context.localAddress());
//                     assertEquals(clients[thread.num].localAddress(), context.remoteAddress());
//                     byte[] bytes = context.ioChannel().availableBytes();
//                     if (bytes != null) {
//                         builders[thread.num].append(bytes);
//                         context.ioChannel().writeBytes(bytes);
//                     }
//                     if (builders[thread.num].size() == data.length) {
//                         readLatches[thread.num].countDown();
//                     }
//                 }
//
//                 @Override
//                 public void channelClose(@Nonnull NetChannelContext context) {
//                     assertFalse(context.isOpen());
//                     context.close();
//                     XThread thread = (XThread) Thread.currentThread();
//                     printFor("client close", thread.num);
//                     assertEquals(clients[thread.num].remoteAddress(), context.localAddress());
//                     assertEquals(clients[thread.num].localAddress(), context.remoteAddress());
//                     closeCount.getAndIncrement();
//                 }
//
//                 @Override
//                 public void exceptionCaught(@Nullable NetChannelContext context, @Nonnull Throwable cause) {
//                     printFor("client exception", ThrowKit.toString(cause));
//                 }
//             })
//             .bind();
//         assertFalse(server.isClosed());
//         assertNotNull(server.localAddress());
//         printFor("server address", server.localAddress());
//
//         for (int i = 0; i < clients.length; i++) {
//             TcpClient client = TcpClient.newBuilder()
//                 .remoteAddress(server.localAddress())
//                 .socketOption(StandardSocketOptions.SO_SNDBUF, 1024)
//                 .bufferSize(1024)
//                 .build();
//             clients[i] = client;
//         }
//
//         // ensure all clients are connected
//         for (int i = 0; i < clients.length; i++) {
//             TcpClient client = clients[i];
//             CountDownLatch openLatch = openLatches[i];
//             assertFalse(client.isConnected());
//             assertFalse(client.isClosed());
//             client.connect();
//             assertTrue(client.isConnected());
//             assertFalse(client.isClosed());
//             expectThrows(NetException.class, client::connect);
//             openLatch.await();
//         }
//
//         // worker threads
//         List<NetServer.Worker> workers = server.workers();
//         for (int i = 0; i < workers.size(); i++) {
//             NetServer.Worker worker = workers.get(i);
//             assertEquals(worker.clientCount(), 1);
//             XThread thread = (XThread) worker.thread();
//             assertEquals(thread.num, i);
//         }
//
//         // send data then read
//         for (int i = 0; i < clients.length; i++) {
//             TcpClient client = clients[i];
//             client.ioChannel().writeBytes(data);
//             readLatches[i].await();
//             BytesBuilder b = new BytesBuilder();
//             while (true) {
//                 client.nextRead();
//                 b.append(client.ioChannel().availableBytes());
//                 if (b.size() == data.length) {
//                     break;
//                 }
//             }
//             client.wakeUpRead();
//             assertEquals(b.toByteArray(), data);
//         }
//
//         // close clients
//         for (TcpClient client : clients) {
//             client.close();
//             assertTrue(client.isClosed());
//             assertFalse(client.isConnected());
//             client.close();
//             assertTrue(client.isClosed());
//             expectThrows(NetException.class, client::connect);
//         }
//
//         // server close
//         assertFalse(server.isClosed());
//         server.close();
//         assertTrue(server.isClosed());
//         server.close();
//         assertTrue(server.isClosed());
//         server.await();
//         closeLatch.await();
//         assertEquals(closeCount.get(), 10);
//     }
//
//     @Test
//     public void testTcpOther() throws Exception {
//         {
//             // test one work thread and exception caught
//             int clientNum = 10;
//             ThreadGate gate = ThreadGate.newThreadGate();
//             CountDownLatch closeLatch = new CountDownLatch(1);
//             gate.close();
//             CountDownLatch openLatch = new CountDownLatch(clientNum);
//             CountDownLatch throwLatch = new CountDownLatch(clientNum * 3);
//             TcpServer server = TcpServer.newBuilder()
//                 .handler(new NetChannelHandler() {
//
//                     @Override
//                     public void channelOpen(@Nonnull NetChannelContext context) throws Exception {
//                         openLatch.countDown();
//                         throw new XException();
//                     }
//
//                     @Override
//                     public void channelClose(@Nonnull NetChannelContext context) throws Exception {
//                         throw new XException();
//                     }
//
//                     @Override
//                     public void channelRead(@Nonnull NetChannelContext context) throws Exception {
//                         throw new XException();
//                     }
//
//                     @Override
//                     public void exceptionCaught(@Nullable NetChannelContext context, @Nonnull Throwable cause) {
//                         if (cause instanceof XException) {
//                             throwLatch.countDown();
//                         }
//                     }
//                 })
//                 .workerThreadNum(1)
//                 .workerThreadFactory(r -> new Thread(() -> {
//                     gate.await();
//                     r.run();
//                     closeLatch.countDown();
//                 }))
//                 .bind();
//             List<TcpClient> clients = new ArrayList<>();
//             for (int i = 0; i < clientNum; i++) {
//                 TcpClient client = TcpClient.newBuilder()
//                     .remoteAddress(server.localAddress())
//                     .build();
//                 client.connect();
//                 clients.add(client);
//             }
//             gate.open();
//             openLatch.await();
//             for (TcpClient client : clients) {
//                 client.close();
//             }
//             throwLatch.await();
//             server.close();
//             closeLatch.await();
//             // exception: doWork()
//             Method doWork = server.getClass().getDeclaredMethod("doWork", VoidCallable.class, boolean.class);
//             doWork.setAccessible(true);
//             doWork.invoke(server, null, true);
//             doWork.invoke(server, (VoidCallable) () -> {
//                 throw new XException();
//             }, false);
//         }
//         {
//             // builder exceptions
//             expectThrows(IllegalArgumentException.class, () ->
//                 TcpServer.newBuilder()
//                     .mainThreadFactory(Thread::new)
//                     .workerThreadNum(0)
//             );
//             expectThrows(IllegalArgumentException.class, () ->
//                 TcpServer.newBuilder().bufferSize(0).bind()
//             );
//             expectThrows(IllegalArgumentException.class, () ->
//                 TcpClient.newBuilder()
//                     .localAddress(new InetSocketAddress(0))
//                     .bufferSize(0)
//                     .build()
//             );
//             expectThrows(NetException.class, () ->
//                 TcpClient.newBuilder().build()
//             );
//             // socket option exceptions
//             expectThrows(NetException.class, () ->
//                 TcpClient.newBuilder()
//                     .remoteAddress(new InetSocketAddress(0))
//                     .socketOption(StandardSocketOptions.IP_MULTICAST_IF, null)
//                     .build()
//             );
//         }
//     }
//
//     private static final class XException extends Exception {
//         private static final long serialVersionUID = 1L;
//     }
// }
