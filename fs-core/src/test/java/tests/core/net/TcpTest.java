package tests.core.net;

import internal.utils.DataGen;
import internal.utils.TestPrint;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.bytes.BytesBuilder;
import space.sunqian.fs.base.exception.ThrowKit;
import space.sunqian.fs.base.function.VoidCallable;
import space.sunqian.fs.base.thread.ThreadGate;
import space.sunqian.fs.base.value.IntVar;
import space.sunqian.fs.net.NetException;
import space.sunqian.fs.net.NetServer;
import space.sunqian.fs.net.tcp.TcpClient;
import space.sunqian.fs.net.tcp.TcpContext;
import space.sunqian.fs.net.tcp.TcpServer;
import space.sunqian.fs.net.tcp.TcpServerHandler;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TcpTest implements DataGen, TestPrint {

    private static class XThread extends Thread {
        private final int num;

        public XThread(int num, Runnable target) {
            super(target);
            this.num = num;
        }
    }

    @Test
    public void testTcpMultiClientCommunication() throws Exception {
        byte[] data = randomBytes(16);
        int workerNum = 5;
        AtomicInteger closeCount = new AtomicInteger();
        CountDownLatch closeLatch = new CountDownLatch(workerNum + 1);

        IntVar workerCount = IntVar.of(0);
        ThreadFactory mainFactory = createMainThreadFactory(closeCount, closeLatch);
        ThreadFactory workerFactory = createWorkerThreadFactory(workerCount, closeCount, closeLatch);

        TcpClient[] clients = new TcpClient[workerNum];
        TcpContext[] contexts = new TcpContext[workerNum];
        BytesBuilder[] builders = new BytesBuilder[workerNum];
        for (int i = 0; i < builders.length; i++) {
            builders[i] = new BytesBuilder();
        }

        CountDownLatch[] openLatches = new CountDownLatch[workerNum];
        for (int i = 0; i < openLatches.length; i++) {
            openLatches[i] = new CountDownLatch(1);
        }
        CountDownLatch[] readLatches = new CountDownLatch[workerNum];
        for (int i = 0; i < readLatches.length; i++) {
            readLatches[i] = new CountDownLatch(1);
        }
        CountDownLatch[] loopLatches = new CountDownLatch[workerNum];
        for (int i = 0; i < loopLatches.length; i++) {
            loopLatches[i] = new CountDownLatch(1);
        }

        InetSocketAddress localhost = new InetSocketAddress("127.0.0.1", 0);
        Object attachment = new Object();

        TcpServer server = createTcpServer(workerNum, mainFactory, workerFactory, localhost, attachment,
            clients, contexts, builders, data, openLatches, readLatches, loopLatches, closeCount);

        assertFalse(server.isClosed());
        assertNotNull(server.localAddress());
        InetSocketAddress serverLocal = server.localAddress();
        printFor("server address", serverLocal);

        // clients connect
        connectClients(clients, server, localhost, openLatches);

        // worker threads
        verifyWorkerThreads(server, workerNum);

        // send data then read
        sendAndReceiveData(clients, data, readLatches);

        // close clients
        closeClients(clients);

        // loop latch
        awaitLoopLatches(loopLatches);

        // server close
        closeServer(server, closeLatch, closeCount, serverLocal);

        // close contexts again
        for (TcpContext context : contexts) {
            context.close();
        }
    }

    @Test
    public void testTcpSingleWorkerThreadWithExceptions() throws Exception {
        int clientNum = 10;
        ThreadGate gate = ThreadGate.newThreadGate();
        CountDownLatch closeLatch = new CountDownLatch(1);
        gate.close();
        CountDownLatch openLatch = new CountDownLatch(clientNum);
        CountDownLatch throwLatch = new CountDownLatch(clientNum * 3);

        TcpServer server = TcpServer.newBuilder()
            .handler(new TcpServerHandler() {
                @Override
                public void channelOpen(@Nonnull TcpContext context) throws Exception {
                    openLatch.countDown();
                    throw new XException();
                }

                @Override
                public void channelClose(@Nonnull TcpContext context) throws Exception {
                    throw new XException();
                }

                @Override
                public void channelRead(@Nonnull TcpContext context) throws Exception {
                    throw new XException();
                }

                @Override
                public void exceptionCaught(@Nullable TcpContext context, @Nonnull Throwable cause) {
                    if (cause instanceof XException) {
                        throwLatch.countDown();
                    }
                }
            })
            .workerThreadNum(1)
            .workerThreadFactory(r -> new Thread(() -> {
                gate.await();
                r.run();
                closeLatch.countDown();
            }))
            .bind();

        List<TcpClient> clients = new ArrayList<>();
        for (int i = 0; i < clientNum; i++) {
            TcpClient client = TcpClient.newBuilder()
                .connect(server.localAddress());
            clients.add(client);
        }

        gate.open();
        openLatch.await();

        for (TcpClient client : clients) {
            client.close();
        }

        throwLatch.await();
        server.close();
        closeLatch.await();

        // Test doWork method
        testDoWorkMethod(server);
    }

    @Test
    public void testTcpNullHandler() throws Exception {
        CountDownLatch latch = new CountDownLatch(2);
        TcpServer server = TcpServer.newBuilder()
            .handler(new TcpServerHandler() {
                @Override
                public void channelOpen(@Nonnull TcpContext context) throws Exception {
                    TcpServerHandler.nullHandler().channelOpen(context);
                }

                @Override
                public void channelClose(@Nonnull TcpContext context) throws Exception {
                    TcpServerHandler.nullHandler().channelClose(context);
                }

                @Override
                public void channelRead(@Nonnull TcpContext context) throws Exception {
                    TcpServerHandler.nullHandler().channelRead(context);
                    latch.countDown();
                    throw new XException();
                }

                @Override
                public void exceptionCaught(@Nullable TcpContext context, @Nonnull Throwable cause) {
                    TcpServerHandler.nullHandler().exceptionCaught(context, cause);
                    if (cause instanceof XException) {
                        latch.countDown();
                    }
                }
            })
            .bind();

        TcpClient client = TcpClient.newBuilder().connect(server.localAddress());
        client.writeString("hello world");

        byte[] bytes = client.availableBytes();
        assertNotNull(bytes);
        assertEquals(0, bytes.length);

        latch.await();
        client.close();
        server.close();
    }

    @Test
    public void testTcpBuilderExceptions() throws Exception {
        // TcpServer builder exceptions
        assertThrows(IllegalArgumentException.class, () ->
            TcpServer.newBuilder()
                .mainThreadFactory(Thread::new)
                .workerThreadNum(0)
        );

        assertThrows(IllegalArgumentException.class, () ->
            TcpServer.newBuilder().ioBufferSize(0).bind()
        );

        // TcpClient builder exceptions
        assertThrows(IllegalArgumentException.class, () ->
            TcpClient.newBuilder()
                .ioBufferSize(0)
                .connect(null)
        );

        assertThrows(NetException.class, () ->
            TcpClient.newBuilder()
                .socketOption(StandardSocketOptions.IP_MULTICAST_IF, null)
                .connect(null)
        );
    }

    @Test
    public void testTcpClientCloseDetection() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        StringBuilder received = new StringBuilder();

        TcpServer server = TcpServer.newBuilder()
            .handler(new TcpServerHandler() {
                @Override
                public void channelOpen(@Nonnull TcpContext context) throws Exception {}

                @Override
                public void channelClose(@Nonnull TcpContext context) throws Exception {}

                @Override
                public void channelRead(@Nonnull TcpContext context) throws Exception {
                    String str = context.availableString();
                    if (str != null) {
                        received.append(str);
                    }
                    if (received.toString().equals("hello world")) {
                        latch.countDown();
                    }
                }

                @Override
                public void exceptionCaught(@Nullable TcpContext context, @Nonnull Throwable cause) {}
            })
            .bind();

        TcpClient client = TcpClient.newBuilder()
            .connect(server.localAddress());
        client.writeString("hello world");
        latch.await();

        assertFalse(client.isClosed());
        assertFalse(server.isClosed());

        server.close();
        assertTrue(server.isClosed());

        assertNull(client.availableString());
        client.close();
        assertTrue(client.isClosed());
    }

    @Test
    public void testTcpServerCloseDetection() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch clientCloseLatch = new CountDownLatch(1);
        StringBuilder received = new StringBuilder();

        TcpServer server = TcpServer.newBuilder()
            .handler(new TcpServerHandler() {
                @Override
                public void channelOpen(@Nonnull TcpContext context) throws Exception {}

                @Override
                public void channelClose(@Nonnull TcpContext context) throws Exception {}

                @Override
                public void channelRead(@Nonnull TcpContext context) throws Exception {
                    String str = context.availableString();
                    if (str != null) {
                        received.append(str);
                    } else {
                        context.close();
                        clientCloseLatch.countDown();
                    }
                    if (received.toString().equals("hello world")) {
                        latch.countDown();
                    }
                }

                @Override
                public void exceptionCaught(@Nullable TcpContext context, @Nonnull Throwable cause) {}
            })
            .bind();

        TcpClient client = TcpClient.newBuilder()
            .connect(server.localAddress());
        client.writeString("hello world");
        latch.await();

        assertFalse(client.isClosed());
        assertFalse(server.isClosed());

        client.close();
        clientCloseLatch.await();

        assertTrue(client.isClosed());
        assertFalse(server.isClosed());

        server.close();
        assertTrue(server.isClosed());
    }

    private static final class XException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    //@Test(timeOut = 100000)
    public void testTelnet() throws Exception {
        TcpServer server = TcpServer.newBuilder()
            .handler(new TcpServerHandler() {
                @Override
                public void channelOpen(@Nonnull TcpContext context) throws Exception {
                    printFor("telnet open", context.clientAddress());
                }

                @Override
                public void channelClose(@Nonnull TcpContext context) throws Exception {
                    printFor("telnet close", context.clientAddress());
                }

                @Override
                public void channelRead(@Nonnull TcpContext context) throws Exception {
                    String msg = context.availableString();
                    if (msg == null) {
                        context.close();
                        return;
                    }
                    printFor("telnet read", context.clientAddress(), ": ", msg);
                }

                @Override
                public void exceptionCaught(@Nullable TcpContext context, @Nonnull Throwable cause) {}
            })
            .bind();

        printFor("Telnet address", server.localAddress());
        server.await();
    }

    private ThreadFactory createMainThreadFactory(AtomicInteger closeCount, CountDownLatch closeLatch) {
        return r -> new Thread(() -> {
            r.run();
            closeCount.incrementAndGet();
            closeLatch.countDown();
        });
    }

    private ThreadFactory createWorkerThreadFactory(IntVar workerCount, AtomicInteger closeCount, CountDownLatch closeLatch) {
        return r -> {
            int num = workerCount.getAndIncrement();
            return new XThread(num, () -> {
                r.run();
                closeCount.incrementAndGet();
                closeLatch.countDown();
            });
        };
    }

    private TcpServer createTcpServer(int workerNum, ThreadFactory mainFactory, ThreadFactory workerFactory,
                                      InetSocketAddress localhost, Object attachment, TcpClient[] clients,
                                      TcpContext[] contexts, BytesBuilder[] builders, byte[] data,
                                      CountDownLatch[] openLatches, CountDownLatch[] readLatches,
                                      CountDownLatch[] loopLatches, AtomicInteger closeCount) {
        return TcpServer.newBuilder()
            .mainThreadFactory(mainFactory)
            .workerThreadNum(workerNum)
            .workerThreadFactory(workerFactory)
            .socketOption(StandardSocketOptions.SO_RCVBUF, 1024)
            .ioBufferSize(1024)
            .handler(new TcpServerHandler() {
                @Override
                public void channelOpen(@Nonnull TcpContext context) throws Exception {
                    SocketChannel channel = context.channel();
                    assertTrue(channel.isOpen());
                    XThread thread = (XThread) Thread.currentThread();
                    openLatches[thread.num].countDown();
                    printFor("client open", thread.num, ", addr: ", channel.getRemoteAddress());
                    contexts[thread.num] = context;
                    context.attach(attachment);
                }

                @Override
                public void channelRead(@Nonnull TcpContext context) throws Exception {
                    SocketChannel channel = context.channel();
                    XThread thread = (XThread) Thread.currentThread();
                    printFor("client read", thread.num);
                    assertEquals(clients[thread.num].remoteAddress(), context.serverAddress());
                    assertEquals(clients[thread.num].localAddress(), context.clientAddress());
                    byte[] bytes = context.availableBytes();
                    if (bytes != null) {
                        builders[thread.num].append(bytes);
                        context.writeBytes(bytes);
                    }
                    if (builders[thread.num].size() == data.length) {
                        readLatches[thread.num].countDown();
                    }
                    assertSame(context.attachment(), attachment);
                }

                @Override
                public void channelLoop(@Nonnull TcpContext context) throws Exception {
                    XThread thread = (XThread) Thread.currentThread();
                    CountDownLatch latch = loopLatches[thread.num];
                    if (latch.getCount() > 0) {
                        latch.countDown();
                        throw new Exception("loop");
                    }
                }

                @Override
                public void channelClose(@Nonnull TcpContext context) throws Exception {
                    SocketChannel channel = context.channel();
                    assertFalse(channel.isOpen());
                    channel.close();
                    XThread thread = (XThread) Thread.currentThread();
                    printFor("client close", thread.num);
                    assertEquals(clients[thread.num].remoteAddress(), context.serverAddress());
                    assertEquals(clients[thread.num].localAddress(), context.clientAddress());
                    closeCount.getAndIncrement();
                }

                @Override
                public void exceptionCaught(@Nullable TcpContext context, @Nonnull Throwable cause) {
                    printFor("client exception", ThrowKit.toString(cause));
                }
            })
            .bind(localhost);
    }

    private void connectClients(TcpClient[] clients, TcpServer server, InetSocketAddress localhost,
                                CountDownLatch[] openLatches) throws Exception {
        for (int i = 0; i < clients.length; i++) {
            TcpClient client = TcpClient.newBuilder()
                .socketOption(StandardSocketOptions.SO_SNDBUF, 1024)
                .ioBufferSize(1024)
                .bind(localhost)
                .connect(server.localAddress());
            CountDownLatch openLatch = openLatches[i];
            openLatch.await();
            assertTrue(client.isConnected());
            assertFalse(client.isClosed());
            clients[i] = client;
        }
    }

    private void verifyWorkerThreads(TcpServer server, int workerNum) {
        List<NetServer.Worker> workers = server.workers();
        for (int i = 0; i < workers.size(); i++) {
            NetServer.Worker worker = workers.get(i);
            assertEquals(1, worker.connectionNumber());
            XThread thread = (XThread) worker.thread();
            assertEquals(thread.num, i);
        }
    }

    private void sendAndReceiveData(TcpClient[] clients, byte[] data, CountDownLatch[] readLatches) throws Exception {
        for (int i = 0; i < clients.length; i++) {
            TcpClient client = clients[i];
            client.writeBytes(data);
            readLatches[i].await();
            BytesBuilder b = new BytesBuilder();
            while (true) {
                client.readWait();
                byte[] bytes = client.availableBytes();
                if (bytes != null) {
                    b.append(bytes);
                }
                if (b.size() == data.length) {
                    break;
                }
            }
            client.readWakeUp();
            assertArrayEquals(b.toByteArray(), data);
        }
    }

    private void closeClients(TcpClient[] clients) {
        for (TcpClient client : clients) {
            InetSocketAddress clientLocal = client.localAddress();
            InetSocketAddress clientRemote = client.remoteAddress();
            client.close();
            assertTrue(client.isClosed());
            assertFalse(client.isConnected());
            client.close();
            assertTrue(client.isClosed());
            assertSame(client.localAddress(), clientLocal);
            assertSame(client.remoteAddress(), clientRemote);
        }
    }

    private void awaitLoopLatches(CountDownLatch[] loopLatches) throws Exception {
        for (CountDownLatch loopLatch : loopLatches) {
            loopLatch.await();
        }
    }

    private void closeServer(TcpServer server, CountDownLatch closeLatch, AtomicInteger closeCount,
                             InetSocketAddress serverLocal) throws Exception {
        assertFalse(server.isClosed());
        server.close();
        assertTrue(server.isClosed());
        server.close();
        assertTrue(server.isClosed());
        // select timeout
        Thread.sleep(100 * 3);
        server.await();
        closeLatch.await();
        assertEquals(11, closeCount.get());
        assertSame(server.localAddress(), serverLocal);
    }

    private void testDoWorkMethod(TcpServer server) throws Exception {
        Method doWork = server.getClass().getDeclaredMethod("doWork", VoidCallable.class, boolean.class);
        doWork.setAccessible(true);
        doWork.invoke(server, null, true);
        doWork.invoke(server, (VoidCallable) () -> {
            throw new XException();
        }, false);
    }
}
