package tests.core.net;

import internal.utils.TestPrint;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.base.function.VoidCallable;
import space.sunqian.fs.net.NetException;
import space.sunqian.fs.net.udp.UdpSender;
import space.sunqian.fs.net.udp.UdpServer;
import space.sunqian.fs.net.udp.UdpServerHandler;

import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UdpTest implements TestPrint {

    @Test
    public void testUdpBasicFunctionality() throws Exception {
        UdpSender sender = UdpSender.newSender(true);
        InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), 0);
        CountDownLatch closeLatch = new CountDownLatch(1);
        CountDownLatch readLatch = new CountDownLatch(2);
        String sentData = "hello";

        UdpServer server = createUdpServer(address, closeLatch, readLatch, sentData);
        printFor("server start", server.localAddress());

        sendDataUsingDifferentMethods(sender, sentData, server.localAddress());

        readLatch.countDown();
        cleanupResources(sender, server, closeLatch);
    }

    @Test
    public void testUdpNullHandler() throws Exception {
        CountDownLatch latch = new CountDownLatch(2);
        UdpServer server = UdpServer.newBuilder()
            .handler(new UdpServerHandler() {
                @Override
                public void channelRead(
                    @Nonnull DatagramChannel channel, byte @Nonnull [] data, @Nonnull SocketAddress address
                ) throws Exception {
                    UdpServerHandler.nullHandler().channelRead(channel, data, address);
                    latch.countDown();
                    throw new XException();
                }

                @Override
                public void exceptionCaught(@Nullable DatagramChannel channel, @Nonnull Throwable cause) {
                    UdpServerHandler.nullHandler().exceptionCaught(channel, cause);
                    if (cause instanceof XException) {
                        latch.countDown();
                    }
                }
            })
            .bind();

        UdpSender sender = UdpSender.newSender();
        InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), server.localAddress().getPort());
        sender.sendString("hello world", address);

        assertFalse(server.isClosed());
        assertEquals(0, server.workers().size());

        latch.await();

        sender.close();
        server.close();

        assertTrue(server.isClosed());

        // Test doWork method
        testDoWorkMethod(server);
    }

    @Test
    public void testUdpBuilderExceptions() throws Exception {
        assertThrows(IllegalArgumentException.class, () ->
            UdpServer.newBuilder()
                .maxPacketSize(0)
                .bind()
                .close()
        );
    }

    @Test
    public void testUdpBroadcast() throws Exception {
        CountDownLatch readLatch = new CountDownLatch(1);
        String sentData = "hello";

        UdpServer server = UdpServer.newBuilder()
            .handler(new UdpServerHandler() {
                @Override
                public void channelRead(
                    @Nonnull DatagramChannel channel, byte @Nonnull [] data, @Nonnull SocketAddress address
                ) throws Exception {
                    String msg = new String(data, CharsKit.defaultCharset());
                    printFor("udp read", address, ": ", msg);
                    assertEquals(sentData, msg);
                    readLatch.countDown();
                }

                @Override
                public void exceptionCaught(@Nullable DatagramChannel channel, @Nonnull Throwable cause) {}
            })
            .bind();

        printFor("Udp address", server.localAddress());

        UdpSender sender = UdpSender.newSender(true);
        printFor("Broadcast address", sender.broadcastAddress());
        printFor("Broadcast address refresh", sender.refreshBroadcastAddress());

        sender.sendBroadcast(sentData, server.localAddress().getPort());
        readLatch.await();

        sender.close();

        testNonBroadcastSenderExceptions(sentData, server.localAddress().getPort());

        server.close();
        server.await();
    }

    private static final class XException extends Exception {
    }

    private UdpServer createUdpServer(InetSocketAddress address, CountDownLatch closeLatch,
                                      CountDownLatch readLatch, String sentData) {
        return UdpServer.newBuilder()
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
                    assertEquals(sentData, msg);
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
    }

    private void sendDataUsingDifferentMethods(UdpSender sender, String sentData, SocketAddress serverAddress) throws Exception {
        sender.sendString(sentData, serverAddress);

        byte[] sentBytes = sentData.getBytes(CharsKit.defaultCharset());
        sender.channel().send(ByteBuffer.wrap(sentBytes), serverAddress);

        sender.sendPacket(new DatagramPacket(sentBytes, 0, sentBytes.length, serverAddress));
    }

    private void cleanupResources(UdpSender sender, UdpServer server, CountDownLatch closeLatch) throws Exception {
        sender.close();
        server.close();
        server.close();
        server.await();
    }

    private void testDoWorkMethod(UdpServer server) throws Exception {
        Method doWork = server.getClass().getDeclaredMethod("doWork", VoidCallable.class, boolean.class);
        doWork.setAccessible(true);
        doWork.invoke(server, null, true);
        doWork.invoke(server, (VoidCallable) () -> {
            throw new XException();
        }, false);
    }

    private void testNonBroadcastSenderExceptions(String sentData, int port) {
        UdpSender nSender = UdpSender.newSender();
        assertThrows(NetException.class, nSender::broadcastAddress);
        assertThrows(NetException.class, nSender::refreshBroadcastAddress);
        assertThrows(NetException.class, () -> nSender.sendBroadcast(sentData, port));
        nSender.close();
    }
}
