package tests.net;

import internal.test.DataTest;
import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.bytes.BytesBuilder;
import space.sunqian.common.base.exception.ThrowKit;
import space.sunqian.common.base.function.callable.VoidCallable;
import space.sunqian.common.base.thread.ThreadGate;
import space.sunqian.common.base.value.IntVar;
import space.sunqian.common.net.NetException;
import space.sunqian.common.net.NetServer;
import space.sunqian.common.net.tcp.TcpClient;
import space.sunqian.common.net.tcp.TcpContext;
import space.sunqian.common.net.tcp.TcpServer;
import space.sunqian.common.net.tcp.TcpServerHandler;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TcpTest implements DataTest, PrintTest {

    @Test
    public void testTcp() throws Exception {
        byte[] data = randomBytes(16);
        int workerNum = 5;
        AtomicInteger closeCount = new AtomicInteger();
        CountDownLatch closeLatch = new CountDownLatch(workerNum + 1);

        class XThread extends Thread {

            private int num;

            public XThread(int num, Runnable target) {
                super(target);
                this.num = num;
            }
        }

        IntVar workerCount = IntVar.of(0);
        ThreadFactory mainFactory = r -> new Thread(() -> {
            r.run();
            closeCount.incrementAndGet();
            closeLatch.countDown();
        });
        ThreadFactory workerFactory = r -> new XThread(workerCount.getAndIncrement(), () -> {
            r.run();
            closeCount.incrementAndGet();
            closeLatch.countDown();
        });

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
        for (int i = 0; i < openLatches.length; i++) {
            readLatches[i] = new CountDownLatch(1);
        }
        CountDownLatch[] loopLatches = new CountDownLatch[workerNum];
        for (int i = 0; i < openLatches.length; i++) {
            loopLatches[i] = new CountDownLatch(1);
        }

        InetSocketAddress localhost = new InetSocketAddress("127.0.0.1", 0);

        Object attachment = new Object();

        TcpServer server = TcpServer.newBuilder()
            .mainThreadFactory(mainFactory)
            .workerThreadNum(workerNum)
            .workerThreadFactory(workerFactory)
            .socketOption(StandardSocketOptions.SO_RCVBUF, 1024)
            .selectTimeout(100)
            .bufferSize(1024)
            .handler(new TcpServerHandler() {

                @Override
                public void channelOpen(@Nonnull TcpContext context) throws Exception {
                    SocketChannel channel = context.channel();
                    assertTrue(channel.isOpen());
                    XThread thread = (XThread) Thread.currentThread();
                    openLatches[thread.num].countDown();
                    printFor("client open",
                        thread.num, ", addr: ", channel.getRemoteAddress());
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
        assertFalse(server.isClosed());
        assertNotNull(server.localAddress());
        InetSocketAddress serverLocal = server.localAddress();
        printFor("server address", serverLocal);

        // clients connect
        for (int i = 0; i < clients.length; i++) {
            TcpClient client = TcpClient.newBuilder()
                .socketOption(StandardSocketOptions.SO_SNDBUF, 1024)
                .bufferSize(1024)
                .bind(localhost)
                .connect(server.localAddress());
            CountDownLatch openLatch = openLatches[i];
            openLatch.await();
            assertTrue(client.isConnected());
            assertFalse(client.isClosed());
            clients[i] = client;
            // printFor("client connect",
            //     i, ", addr: ", client.channel().getRemoteAddress());
        }

        // worker threads
        List<NetServer.Worker> workers = server.workers();
        for (int i = 0; i < workers.size(); i++) {
            NetServer.Worker worker = workers.get(i);
            assertEquals(1, worker.connectionNumber());
            XThread thread = (XThread) worker.thread();
            assertEquals(thread.num, i);
        }

        // send data then read
        for (int i = 0; i < clients.length; i++) {
            TcpClient client = clients[i];
            client.writeBytes(data);
            readLatches[i].await();
            BytesBuilder b = new BytesBuilder();
            while (true) {
                client.awaitReadable();
                b.append(client.availableBytes());
                if (b.size() == data.length) {
                    break;
                }
            }
            client.wakeUpReadable();
            assertArrayEquals(b.toByteArray(), data);
        }

        // close clients
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

        // loop latch
        for (CountDownLatch loopLatch : loopLatches) {
            loopLatch.await();
        }

        // server close
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

        // close contexts again
        for (TcpContext context : contexts) {
            context.close();
        }
    }

    @Test
    public void testTcpOther() throws Exception {
        {
            // test one work thread and exception caught
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
            // exception: doWork()
            Method doWork = server.getClass().getDeclaredMethod("doWork", VoidCallable.class, boolean.class);
            doWork.setAccessible(true);
            doWork.invoke(server, null, true);
            doWork.invoke(server, (VoidCallable) () -> {
                throw new XException();
            }, false);
        }
        {
            // null handler
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
            assertEquals(client.availableBuffer(), ByteBuffer.allocate(0));
            latch.await();
            client.close();
            server.close();
        }
        {
            // builder exceptions
            assertThrows(IllegalArgumentException.class, () ->
                TcpServer.newBuilder()
                    .mainThreadFactory(Thread::new)
                    .workerThreadNum(0)
            );
            assertThrows(IllegalArgumentException.class, () ->
                TcpServer.newBuilder().bufferSize(0).bind()
            );
            assertThrows(IllegalArgumentException.class, () ->
                TcpServer.newBuilder().selectTimeout(-1).bind()
            );
            // socket option exceptions
            assertThrows(IllegalArgumentException.class, () ->
                TcpClient.newBuilder()
                    .bufferSize(0)
                    .connect(null)
            );
            assertThrows(NetException.class, () ->
                TcpClient.newBuilder()
                    .socketOption(StandardSocketOptions.IP_MULTICAST_IF, null)
                    .connect(null)
            );
        }
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
                public void exceptionCaught(@Nullable TcpContext context, @Nonnull Throwable cause) {

                }
            })
            .bind();
        printFor("Telnet address", server.localAddress());
        server.await();
    }
}
