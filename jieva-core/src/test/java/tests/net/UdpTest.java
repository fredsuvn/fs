package tests.net;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.function.callable.VoidCallable;
import xyz.sunqian.common.net.udp.UdpSender;
import xyz.sunqian.common.net.udp.UdpServer;
import xyz.sunqian.common.net.udp.UdpServerHandler;
import xyz.sunqian.test.PrintTest;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.CountDownLatch;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class UdpTest implements PrintTest {

    @Test
    public void testUdp() throws Exception {
        UdpSender sender = UdpSender.newSender(true);
        InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), 0);
        CountDownLatch closeLatch = new CountDownLatch(1);
        CountDownLatch readLatch = new CountDownLatch(2);
        UdpServer server = UdpServer.newBuilder()
            .maxPacketSize(1024)
            .mainThreadFactory(r -> new Thread(() -> {
                r.run();
                closeLatch.countDown();
            }))
            .handler(new UdpServerHandler() {

                @Override
                public void channelRead(
                    @Nonnull DatagramChannel channel, byte @Nonnull [] data, @Nonnull SocketAddress address
                ) throws Exception {
                    String msg = new String(data, CharsKit.defaultCharset());
                    printFor("udp read", msg);
                    assertEquals(msg, "hello");
                    throw new XException();
                }

                @Override
                public void exceptionCaught(@Nullable DatagramChannel channel, @Nonnull Throwable cause) {
                    if (channel != null) {
                        assertTrue(cause instanceof XException);
                        readLatch.countDown();
                    }
                }
            })
            .socketOption(StandardSocketOptions.SO_BROADCAST, true)
            .bind(address);
        printFor("server start", server.localAddress());
        sender.sendString("hello", server.localAddress());
        sender.datagramChannel().send(ByteBuffer.wrap("hello".getBytes()), server.localAddress());
        readLatch.countDown();
        sender.close();
        server.close();
        server.close();
        server.await();
    }

    @Test
    public void testUdpOther() throws Exception {
        {
            // null handler
            UdpServer server = UdpServer.newBuilder().bind();
            UdpSender sender = UdpSender.newSender();
            InetSocketAddress address =
                new InetSocketAddress(InetAddress.getLocalHost(), server.localAddress().getPort());
            sender.sendString("hello world", address);
            assertFalse(server.isClosed());
            assertEquals(server.workers().size(), 0);
            server.close();
            assertTrue(server.isClosed());
            // exception: doWork()
            Method doWork = server.getClass().getDeclaredMethod("doWork", VoidCallable.class, boolean.class);
            doWork.setAccessible(true);
            doWork.invoke(server, null, true);
            doWork.invoke(server, (VoidCallable) () -> {
                throw new XException();
            }, false);
        }
        {
            // builder exceptions
            expectThrows(IllegalArgumentException.class, () ->
                UdpServer.newBuilder()
                    .maxPacketSize(0)
                    .bind()
                    .close()
            );
            // expectThrows(NetException.class, () ->
            //     UdpServer.newBuilder()
            //         .socketOption(StandardSocketOptions.SO_LINGER, 111)
            // );
        }
    }

    private static final class XException extends Exception {
    }
}
