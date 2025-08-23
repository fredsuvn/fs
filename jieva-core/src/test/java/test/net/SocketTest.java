package test.net;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.exception.ThrowKit;
import xyz.sunqian.common.base.thread.ThreadGate;
import xyz.sunqian.common.base.value.IntVar;
import xyz.sunqian.common.net.NetChannelContext;
import xyz.sunqian.common.net.NetChannelHandler;
import xyz.sunqian.common.net.NetChannelType;
import xyz.sunqian.common.net.NetException;
import xyz.sunqian.common.net.NetServer;
import xyz.sunqian.common.net.socket.SocketKit;
import xyz.sunqian.common.net.socket.TcpClient;
import xyz.sunqian.common.net.socket.TcpServer;
import xyz.sunqian.test.DataTest;
import xyz.sunqian.test.PrintTest;

import java.net.StandardSocketOptions;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class SocketTest implements DataTest, PrintTest {

    @Test
    public void testTcpMain() throws Exception {
        int workerNum = 5;
        AtomicInteger closeCount = new AtomicInteger();
        CountDownLatch closeLatch = new CountDownLatch(workerNum);

        class XThread extends Thread {

            private int num;

            public XThread(int num, Runnable target) {
                super(target);
                this.num = num;
            }
        }

        IntVar workerCount = IntVar.of(0);
        ThreadFactory workerFactory = r -> new XThread(workerCount.getAndIncrement(), () -> {
            r.run();
            closeCount.incrementAndGet();
            closeLatch.countDown();
        });

        TcpClient[] clients = new TcpClient[workerNum];
        BytesBuilder[] builders = new BytesBuilder[workerNum];
        for (int i = 0; i < builders.length; i++) {
            builders[i] = new BytesBuilder();
        }

        CountDownLatch[] openLatches = new CountDownLatch[workerNum];
        for (int i = 0; i < openLatches.length; i++) {
            openLatches[i] = new CountDownLatch(1);
        }

        TcpServer server = SocketKit.tcpServerBuilder()
            .workerThreadNum(workerNum)
            .workerThreadFactory(workerFactory)
            .socketOption(StandardSocketOptions.SO_RCVBUF, 1024)
            .bufferSize(1024)
            .backlog(50)
            .handler(new NetChannelHandler() {

                @Override
                public void channelOpen(@Nonnull NetChannelContext context) {
                    assertTrue(context.isOpen());
                    assertSame(context.channelType(), NetChannelType.tcpIp());
                    XThread thread = (XThread) Thread.currentThread();
                    openLatches[thread.num].countDown();
                    printFor("client open", thread.num);
                    assertEquals(clients[thread.num].remoteAddress(), context.localAddress());
                    assertEquals(clients[thread.num].localAddress(), context.remoteAddress());
                }

                @Override
                public void channelRead(@Nonnull NetChannelContext context) {
                    XThread thread = (XThread) Thread.currentThread();
                    printFor("client read", thread.num);
                    assertEquals(clients[thread.num].remoteAddress(), context.localAddress());
                    assertEquals(clients[thread.num].localAddress(), context.remoteAddress());
                    byte[] bytes = context.ioChannel().nextBytes();
                    if (bytes != null) {
                        builders[thread.num].append(bytes);
                    }
                }

                @Override
                public void channelClose(@Nonnull NetChannelContext context) {
                    assertFalse(context.isOpen());
                    context.close();
                    XThread thread = (XThread) Thread.currentThread();
                    printFor("client close", thread.num);
                    assertEquals(clients[thread.num].remoteAddress(), context.localAddress());
                    assertEquals(clients[thread.num].localAddress(), context.remoteAddress());
                    closeCount.getAndIncrement();
                }

                @Override
                public void exceptionCaught(@Nullable NetChannelContext context, @Nonnull Throwable cause) {
                    printFor("client exception", ThrowKit.toString(cause));
                }
            })
            .build();
        assertFalse(server.isStarted());
        server.start();
        assertTrue(server.isStarted());
        assertFalse(server.isClosed());
        expectThrows(NetException.class, server::start);
        assertTrue(server.isStarted());
        assertFalse(server.isClosed());
        printFor("server address", server.localAddress());

        for (int i = 0; i < clients.length; i++) {
            TcpClient client = SocketKit.tcpClientBuilder()
                .remoteAddress(server.localAddress())
                .socketOption(StandardSocketOptions.SO_SNDBUF, 1024)
                .bufferSize(1024)
                .build();
            clients[i] = client;
        }

        // ensure all clients are connected
        for (int i = 0; i < clients.length; i++) {
            TcpClient client = clients[i];
            CountDownLatch openLatch = openLatches[i];
            assertFalse(client.isConnected());
            assertFalse(client.isClosed());
            client.connect();
            assertTrue(client.isConnected());
            assertFalse(client.isClosed());
            expectThrows(NetException.class, client::connect);
            openLatch.await();
        }

        // worker threads
        List<NetServer.Worker> workers = server.workers();
        for (int i = 0; i < workers.size(); i++) {
            NetServer.Worker worker = workers.get(i);
            assertEquals(worker.clientCount(), 1);
            XThread thread = (XThread) worker.thread();
            assertEquals(thread.num, i);
        }

        // send data then close
        byte[] data = randomBytes(16);
        for (TcpClient client : clients) {
            client.ioChannel().writeBytes(data);
        }

        // close clients
        for (TcpClient client : clients) {
            client.close();
            assertTrue(client.isClosed());
            assertFalse(client.isConnected());
            client.close();
            assertTrue(client.isClosed());
            expectThrows(NetException.class, client::connect);
        }

        // server close
        assertFalse(server.isClosed());
        server.close();
        assertTrue(server.isClosed());
        server.close();
        assertTrue(server.isClosed());
        assertFalse(server.isStarted());
        expectThrows(NetException.class, server::start);
        server.await();
        closeLatch.await();
        assertEquals(closeCount.get(), 10);
    }

    @Test
    public void testTcpOther() {
        NetChannelHandler emptyHandler = new NetChannelHandler() {

            @Override
            public void channelOpen(@Nonnull NetChannelContext context) {
            }

            @Override
            public void channelClose(@Nonnull NetChannelContext context) {
            }

            @Override
            public void channelRead(@Nonnull NetChannelContext context) {
            }

            @Override
            public void exceptionCaught(@Nullable NetChannelContext context, @Nonnull Throwable cause) {
            }
        };

        ThreadGate gate = ThreadGate.newThreadGate();
        CountDownLatch closeLatch = new CountDownLatch(1);
        gate.close();
        TcpServer server = SocketKit.tcpServerBuilder()
            .handler(emptyHandler)
            .workerThreadNum(1)
            .workerThreadFactory(r-> new Thread(()->{
                gate.await();
                r.run();
                closeLatch.countDown();
            }))
            .build();
        server.start();
        for (int i = 0; i < 10; i++) {
            TcpClient client = SocketKit.tcpClientBuilder().remoteAddress(server.localAddress()).build();
            client.connect();
        }
        gate.open();
        //server.
    }
}
