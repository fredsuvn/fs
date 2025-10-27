package tests.net;

import org.junit.jupiter.api.Test;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.chars.CharsKit;
import space.sunqian.common.function.callable.VoidCallable;
import space.sunqian.common.net.NetException;
import space.sunqian.common.net.udp.UdpSender;
import space.sunqian.common.net.udp.UdpServer;
import space.sunqian.common.net.udp.UdpServerHandler;
import internal.test.PrintTest;

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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UdpTest implements PrintTest {

    @Test
    public void testUdp() throws Exception {
        UdpSender sender = UdpSender.newSender(true);
        InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), 0);
        CountDownLatch closeLatch = new CountDownLatch(1);
        CountDownLatch readLatch = new CountDownLatch(2);
        String sentData = "hello";
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
                    assertEquals(msg, sentData);
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
        sender.sendString(sentData, server.localAddress());
        byte[] sentBytes = sentData.getBytes(CharsKit.defaultCharset());
        sender.channel().send(ByteBuffer.wrap(sentBytes), server.localAddress());
        sender.sendPacket(new DatagramPacket(sentBytes, 0, sentBytes.length, server.localAddress()));
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
            InetSocketAddress address =
                new InetSocketAddress(InetAddress.getLocalHost(), server.localAddress().getPort());
            sender.sendString("hello world", address);
            assertFalse(server.isClosed());
            assertEquals(server.workers().size(), 0);
            latch.await();
            sender.close();
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
            assertThrows(IllegalArgumentException.class, () ->
                UdpServer.newBuilder()
                    .maxPacketSize(0)
                    .bind()
                    .close()
            );
            // assertThrows(NetException.class, () ->
            //     UdpServer.newBuilder()
            //         .socketOption(StandardSocketOptions.SO_LINGER, 111)
            // );
        }
    }

    private static final class XException extends Exception {
    }

    @Test
    public void testBroadcast() throws Exception {
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
                    assertEquals(msg, sentData);
                    readLatch.countDown();
                }

                @Override
                public void exceptionCaught(@Nullable DatagramChannel channel, @Nonnull Throwable cause) {
                }
            })
            .bind();
        printFor("Udp address", server.localAddress());
        UdpSender sender = UdpSender.newSender(true);
        printFor("Broadcast address", sender.broadcastAddress());
        printFor("Broadcast address refresh", sender.refreshBroadcastAddress());
        sender.sendBroadcast(sentData, server.localAddress().getPort());
        readLatch.await();
        sender.close();
        UdpSender nSender = UdpSender.newSender();
        assertThrows(NetException.class, nSender::broadcastAddress);
        assertThrows(NetException.class, () -> nSender.sendBroadcast(sentData, server.localAddress().getPort()));
        server.close();
        server.await();
    }
}
