package internal.samples;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.net.http.HttpCaller;
import space.sunqian.fs.net.http.HttpReq;
import space.sunqian.fs.net.http.HttpResp;
import space.sunqian.fs.net.tcp.TcpContext;
import space.sunqian.fs.net.tcp.TcpServer;
import space.sunqian.fs.net.tcp.TcpServerHandler;

/**
 * Sample: Networking Usage
 * <p>
 * Purpose: Demonstrate how to use the networking utilities provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     Create a TCP server with custom handler
 *   </li>
 *   <li>
 *     Make HTTP requests with different methods and headers
 *   </li>
 *   <li>
 *     Handle network exceptions and errors
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link TcpServer}: High-performance TCP server implementation
 *   </li>
 *   <li>
 *     {@link HttpCaller}: HTTP client with JDK version-adaptive implementations
 *   </li>
 *   <li>
 *     {@link IOKit}: Utility for I/O operations
 *   </li>
 * </ul>
 */
public class NetSample {

    public static void main(String[] args) {
        demonstrateTcpServerWithHttpClient();
    }

    /**
     * Demonstrates creating a TCP server and making an HTTP request to it.
     */
    public static void demonstrateTcpServerWithHttpClient() {
        System.out.println("=== TCP Server with HTTP Client Demonstration ===");

        // Create and start TCP server
        TcpServer tcpServer = TcpServer.newBuilder()
            .handler(new TcpServerHandler() {
                @Override
                public void channelOpen(@Nonnull TcpContext context) throws Exception {
                    System.out.println("Client connected: " + context.channel().getRemoteAddress());
                }

                @Override
                public void channelClose(@Nonnull TcpContext context) throws Exception {
                    System.out.println("Client disconnected: " + context.channel().getRemoteAddress());
                }

                @Override
                public void channelRead(@Nonnull TcpContext context) throws Exception {
                    // Read message from client
                    String msg = IOKit.availableString(context.channel());
                    System.out.println("Received message: " + msg);

                    // Send HTTP response
                    String response = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "Content-Length: 5\r\n" +
                        "\r\n" +
                        "Hello";
                    IOKit.write(context.channel(), response);
                    System.out.println("Sent response: " + response);
                }

                @Override
                public void exceptionCaught(@Nullable TcpContext context, @Nonnull Throwable cause) {
                    System.err.println("Exception caught: " + cause.getMessage());
                }
            })
            .bind();

        System.out.println("TCP server started on port: " + tcpServer.localAddress().getPort());

        try {
            // Create HTTP caller
            HttpCaller httpCaller = HttpCaller.newHttpCaller();

            // Make HTTP POST request
            HttpResp resp = httpCaller.request(HttpReq.newBuilder()
                .url("http://127.0.0.1:" + tcpServer.localAddress().getPort())
                .method("POST")
                .header("Content-Type", "text/plain; charset=UTF-8")
                .body("hello world!")
                .build()
            );

            // Print response
            System.out.println("HTTP Response Status: " + resp.statusCode());
            System.out.println("HTTP Response Body: " + resp.bodyString());

        } catch (Exception e) {
            System.err.println("HTTP request failed: " + e.getMessage());
        } finally {
            // Close TCP server
            tcpServer.close();
            System.out.println("TCP server closed");
        }
    }
}
