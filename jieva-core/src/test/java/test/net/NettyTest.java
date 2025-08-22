package test.net;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.ThrowKit;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.net.NetChannelContext;
import xyz.sunqian.common.net.NetChannelHandler;
import xyz.sunqian.common.net.socket.SocketKit;
import xyz.sunqian.common.net.socket.TcpServer;
import xyz.sunqian.test.PrintTest;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class NettyTest implements PrintTest {

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
}
