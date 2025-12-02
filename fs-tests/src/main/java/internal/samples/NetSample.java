package internal.samples;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.io.IOKit;
import space.sunqian.common.net.http.HttpCaller;
import space.sunqian.common.net.http.HttpReq;
import space.sunqian.common.net.http.HttpResp;
import space.sunqian.common.net.tcp.TcpContext;
import space.sunqian.common.net.tcp.TcpServer;
import space.sunqian.common.net.tcp.TcpServerHandler;

public class NetSample {

    public static void main(String[] args) {
        TcpServer tcpServer = TcpServer.newBuilder()
            .handler(new TcpServerHandler() {
                @Override
                public void channelOpen(@Nonnull TcpContext context) throws Exception {

                }

                @Override
                public void channelClose(@Nonnull TcpContext context) throws Exception {

                }

                @Override
                public void channelRead(@Nonnull TcpContext context) throws Exception {
                    String msg = IOKit.availableString(context.channel());
                    System.out.println("Receives message: " + msg);
                    String response = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "Content-Length: 5\r\n" +
                        "\r\n" +
                        "Hello";
                    IOKit.write(context.channel(), response);
                }

                @Override
                public void exceptionCaught(@Nullable TcpContext context, @Nonnull Throwable cause) {

                }
            })
            .bind();
        HttpCaller httpCaller = HttpCaller.newHttpCaller();
        HttpResp resp = httpCaller.request(HttpReq.newBuilder()
            .url("http://127.0.0.1:" + tcpServer.localAddress().getPort())
            .method("POST")
            .header("Content-Type", "text/plain; charset=UTF-8")
            .body("hello world!")
            .build()
        );
        System.out.println(resp.bodyString());
        tcpServer.close();
    }
}
