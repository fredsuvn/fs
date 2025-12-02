package internal.tests.api;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.io.IOKit;
import space.sunqian.common.net.tcp.TcpContext;
import space.sunqian.common.net.tcp.TcpServer;
import space.sunqian.common.net.tcp.TcpServerHandler;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public abstract class TcpServerApi {

    public static TcpServerApi createServer(String serverType) {
        return switch (serverType) {
            case "fs" -> new FsServer();
            case "netty" -> new NettyServer();
            default -> throw new IllegalArgumentException();
        };
    }

    public abstract InetSocketAddress address();

    public abstract void shutdown();

    private static final class FsServer extends TcpServerApi {

        private final TcpServer server = TcpServer.newBuilder()
            .workerThreadNum(10)
            .handler(new TcpServerHandler() {

                @Override
                public void channelOpen(@Nonnull TcpContext context) throws Exception {

                }

                @Override
                public void channelClose(@Nonnull TcpContext context) throws Exception {

                }

                @Override
                public void channelRead(@Nonnull TcpContext context) throws Exception {
                    byte[] bytes = IOKit.availableBytes(context.channel());
                    if (bytes != null) {
                        IOKit.write(context.channel(), ByteBuffer.wrap(bytes));
                    }
                }

                @Override
                public void exceptionCaught(@Nullable TcpContext context, @Nonnull Throwable cause) {

                }
            })
            .bind();

        @Override
        public InetSocketAddress address() {
            return server.localAddress();
        }

        @Override
        public void shutdown() {
            server.close();
        }
    }

    private static final class NettyServer extends TcpServerApi {

        private final EventLoopGroup bossGroup;
        private final EventLoopGroup workerGroup;
        private final ChannelFuture future;

        {
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup(10);
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                            .addLast(new SimpleEchoHandler());
                    }
                });
            ChannelFuture future;
            try {
                future = bootstrap.bind(new InetSocketAddress(0)).sync();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            this.bossGroup = bossGroup;
            this.workerGroup = workerGroup;
            this.future = future;
        }

        @Override
        public InetSocketAddress address() {
            return (InetSocketAddress) future.channel().localAddress();
        }

        @Override
        public void shutdown() {
            try {
                future.channel().close().sync();
                bossGroup.shutdownGracefully().sync();
                workerGroup.shutdownGracefully().sync();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        private static class SimpleEchoHandler extends ChannelInboundHandlerAdapter {

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                ByteBuf buf = (ByteBuf) msg;
                try {
                    byte[] bytes = new byte[buf.readableBytes()];
                    buf.readBytes(bytes);
                    ctx.writeAndFlush(Unpooled.wrappedBuffer(bytes));
                } finally {
                    buf.release();
                }
            }

            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) {
                ctx.flush();
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                ctx.close();
            }
        }
    }
}
