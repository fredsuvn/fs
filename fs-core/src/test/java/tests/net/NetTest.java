package tests.net;

import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.net.NetException;
import space.sunqian.fs.net.NetKit;
import space.sunqian.fs.net.NetSelector;
import space.sunqian.fs.net.tcp.TcpClient;
import space.sunqian.fs.net.tcp.TcpContext;
import space.sunqian.fs.net.tcp.TcpServer;
import space.sunqian.fs.net.tcp.TcpServerHandler;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NetTest implements PrintTest {

    @Test
    public void testBroadcastAddress() throws Exception {
        printFor("allBroadcastAddresses", NetKit.allBroadcastAddresses());
        printFor("getBroadcastAddress", NetKit.getBroadcastAddress());
    }

    @Test
    public void testNetSelector() throws Exception {
        {
            // base
            CountDownLatch latch = new CountDownLatch(1);
            CountDownLatch clientCloseLatch = new CountDownLatch(1);
            StringBuilder received = new StringBuilder();
            TcpServer server = TcpServer.newBuilder()
                .handler(new TcpServerHandler() {

                    @Override
                    public void channelOpen(@Nonnull TcpContext context) throws Exception {
                    }

                    @Override
                    public void channelClose(@Nonnull TcpContext context) throws Exception {
                    }

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
                    public void exceptionCaught(@Nullable TcpContext context, @Nonnull Throwable cause) {
                    }
                })
                .bind();
            TcpClient client = TcpClient.newBuilder()
                .connect(server.localAddress());
            client.writeString("hello world");
            latch.await();
            client.close();
            clientCloseLatch.await();
            // no cancel
            NetSelector selector = NetSelector.open();
            Selector s1 = selector.selector();
            selector.cancel(client.channel());
            // keys
            assertEquals(0, selector.keys().size());
            // rebuild
            Selector s2 = selector.selector();
            assertSame(s1, s2);
            SocketChannel c1 = SocketChannel.open();
            c1.configureBlocking(false);
            c1.register(s2, SelectionKey.OP_READ);
            SocketChannel c2 = SocketChannel.open();
            c2.configureBlocking(false);
            c2.register(s2, SelectionKey.OP_READ);
            Set<SelectionKey> keys = new HashSet<>(s2.keys());
            selector.cancel(c2);
            int i = 0;
            while (i++ < 512) {
                selector.select(1);
            }
            Selector s3 = selector.selector();
            assertNotSame(s2, selector.selector());
            assertNotSame(s2, s3);
            assertSame(s3.keys(), selector.keys());
            assertEquals(keys.size() - 1, s3.keys().size());
            assertSame(c1, s3.keys().iterator().next().channel());
            selector.close();
        }
    }

    @Test
    public void testException() throws Exception {
        {
            // NetException
            assertThrows(NetException.class, () -> {
                throw new NetException();
            });
            assertThrows(NetException.class, () -> {
                throw new NetException("");
            });
            assertThrows(NetException.class, () -> {
                throw new NetException("", new RuntimeException());
            });
            assertThrows(NetException.class, () -> {
                throw new NetException(new RuntimeException());
            });
        }
    }
}