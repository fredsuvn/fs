package xyz.sunqian.common.test;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.value.LongVar;
import xyz.sunqian.common.net.tcp.TcpContext;
import xyz.sunqian.common.net.tcp.TcpServer;
import xyz.sunqian.common.net.tcp.TcpServerHandler;

public class TestMain {

    public static void main(String[] args) {
        TcpServer server = TcpServer.newBuilder()
            .selectTimeout(1000)
            .workerThreadNum(5)
            .handler(new TcpServerHandler() {

                @Override
                public void channelOpen(@Nonnull TcpContext context) throws Exception {
                    System.out.println("channelOpen: " + context.clientAddress());
                    LongVar lastTime = LongVar.of(System.currentTimeMillis());
                    context.attach(lastTime);
                }

                @Override
                public void channelClose(@Nonnull TcpContext context) throws Exception {
                    System.out.println("channelClose: " + context.clientAddress());
                }

                @Override
                public void channelRead(@Nonnull TcpContext context) throws Exception {
                    String msg = context.availableString();
                    if (msg == null) {
                        context.close();
                        return;
                    }
                    LongVar lastTime = (LongVar) context.attachment();
                    lastTime.set(System.currentTimeMillis());
                    System.out.println("channelRead: " + msg);
                }

                @Override
                public void channelLoop(@Nonnull TcpContext context) throws Exception {
                    LongVar lastTime = (LongVar) context.attachment();
                    long now = System.currentTimeMillis();
                    long prev = lastTime.get();
                    if (now - prev > 10000) {
                        System.out.println("timeout: " + now + " - " + prev + " = " + (now - prev));
                        context.close();
                    }
                }

                @Override
                public void exceptionCaught(@Nullable TcpContext context, @Nonnull Throwable cause) {
                }
            })
            .bind();
        System.out.println("server: " + server.localAddress());
        server.await();
    }
}
