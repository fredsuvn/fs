package test.net;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.net.tcp.TcpNetEndpoint;
import xyz.sunqian.common.net.tcp.TcpNetListener;
import xyz.sunqian.common.net.tcp.TcpNetServer;

import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class NetTest {

    @Test
    public void testTcpNet() throws Exception {
        TcpListener listener = new TcpListener(new CountDownLatch(5));
        testTcpNet(TcpNetServer.newServer(listener), 5, listener);
    }

    private void testTcpNet(TcpNetServer server, int clientNum, TcpListener listener) throws Exception {
        server.start();
        for (int i = 0; i < clientNum; i++) {
            Socket socket = new Socket(InetAddress.getLocalHost(), server.getPort());
            socket.getOutputStream().write("hello".getBytes(CharsKit.defaultCharset()));
            byte[] bytes = new byte[5];
            IOKit.readTo(socket.getInputStream(), bytes);
            assertEquals(bytes, "hello".getBytes(CharsKit.defaultCharset()));
            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();
        }
        listener.latch.await();
        assertEquals(listener.open.get(), 1);
        assertEquals(listener.close.get(), 0);
        assertEquals(listener.connection.get(), clientNum);
        assertEquals(listener.disconnection.get(), clientNum);
        assertEquals(listener.msgList.size(), clientNum);
        for (String s : listener.msgList) {
            assertEquals(s, "hello");
        }
        assertEquals(listener.expList.size(), clientNum);
        for (Throwable e : listener.expList) {
            assertTrue(e instanceof LsException);
        }
        server.close();
        assertEquals(listener.close.get(), 1);
    }

    private static final class TcpListener implements TcpNetListener {

        final AtomicInteger open = new AtomicInteger();
        final AtomicInteger close = new AtomicInteger();
        final AtomicInteger connection = new AtomicInteger();
        final AtomicInteger disconnection = new AtomicInteger();
        final List<Throwable> expList = Collections.synchronizedList(new ArrayList<>());
        final List<String> msgList = Collections.synchronizedList(new ArrayList<>());

        final CountDownLatch latch;

        private TcpListener(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onOpen() throws Exception {
            open.incrementAndGet();
        }

        @Override
        public void onClose() throws Exception {
            close.incrementAndGet();
        }

        @Override
        public void onConnection(@Nonnull TcpNetEndpoint endpoint) throws Exception {
            connection.incrementAndGet();
            System.out.println(endpoint.getAddress());
        }

        @Override
        public void onDisconnection(@Nonnull TcpNetEndpoint endpoint, boolean closedByRemote) throws Exception {
            disconnection.incrementAndGet();
            latch.countDown();
        }

        @Override
        public void onException(@Nonnull TcpNetServer server, @Nullable TcpNetEndpoint endpoint, Throwable throwable) {
            expList.add(throwable);
        }

        @Override
        public void onMessage(@Nonnull TcpNetEndpoint endpoint, @Nonnull ByteBuffer msg) throws Exception {
            String msgString = BufferKit.string(msg, CharsKit.defaultCharset());
            System.out.println(msgString);
            msgList.add(msgString);
            endpoint.send(ByteBuffer.wrap(msgString.getBytes(CharsKit.defaultCharset())));
            throw new LsException();
        }
    }

    private static final class LsException extends Exception {
    }
}
