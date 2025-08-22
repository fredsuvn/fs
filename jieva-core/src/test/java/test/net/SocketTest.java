package test.net;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.base.exception.ThrowKit;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.net.NetChannelContext;
import xyz.sunqian.common.net.NetChannelHandler;
import xyz.sunqian.common.net.socket.SocketChannelReader;
import xyz.sunqian.common.net.socket.SocketChannelWriter;
import xyz.sunqian.common.net.socket.SocketKit;
import xyz.sunqian.common.net.socket.TcpServer;
import xyz.sunqian.test.DataTest;
import xyz.sunqian.test.PrintTest;

import java.io.ByteArrayInputStream;
import java.nio.channels.Channels;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class SocketTest implements DataTest, PrintTest {

    @Test
    public void testNet() {
        TcpServer server = SocketKit.tcpServerBuilder()
            .workThreadNum(1)
            .handler(new NetChannelHandler() {
                @Override
                public void channelOpen(@Nonnull NetChannelContext context) throws Exception {
                    printFor("client open", context.remoteAddress());
                }

                @Override
                public void channelClose(@Nonnull NetChannelContext context) throws Exception {
                    printFor("client close", context.remoteAddress());
                }

                @Override
                public void channelRead(@Nonnull NetChannelContext context) {
                    printFor("client read", context.reader().nextString());
                }

                @Override
                public void exceptionCaught(@Nullable NetChannelContext context, @Nonnull Throwable cause) {
                    printFor("client exception", ThrowKit.toString(cause));
                }
            })
            .build();
        new Thread(server::start).start();
        printFor("server address", server.localAddress());
        server.await();
    }

    @Test
    public void testChannelReader() {
        byte[] bytes = "hello".getBytes(CharsKit.defaultCharset());
        {
            SocketChannelReader reader = new SocketChannelReader(
                Channels.newChannel(new ByteArrayInputStream(bytes)), IOKit.bufferSize()
            );
            assertTrue(reader.isOpen());
            assertEquals(reader.nextString(), "hello");
            assertFalse(reader.isOpen());
            assertNull(reader.nextString());
        }
        {
            SocketChannelReader reader = new SocketChannelReader(
                Channels.newChannel(new ByteArrayInputStream(bytes)), IOKit.bufferSize()
            );
            assertTrue(reader.isOpen());
            assertEquals(reader.nextBytes(), "hello".getBytes(CharsKit.defaultCharset()));
            assertFalse(reader.isOpen());
            assertNull(reader.nextBytes());
        }
    }

    @Test
    public void testChannelWriter() throws Exception {
        BytesBuilder builder = new BytesBuilder();
        {
            SocketChannelWriter writer = new SocketChannelWriter(Channels.newChannel(builder));
            writer.writeString("hello");
            writer.writeString(" ");
            writer.writeString("world");
            assertEquals(builder.toByteArray(), "hello world".getBytes(CharsKit.defaultCharset()));
            assertTrue(writer.isOpen());
            writer.close();
            assertFalse(writer.isOpen());
            expectThrows(IORuntimeException.class, () -> writer.writeString("hello"));
        }
    }
}
