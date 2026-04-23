package internal.samples;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.net.tcp.TcpClient;
import space.sunqian.fs.net.tcp.TcpContext;
import space.sunqian.fs.net.tcp.TcpServer;
import space.sunqian.fs.net.tcp.TcpServerHandler;

/**
 * Sample: TCP Usage
 * <p>
 * Purpose: Demonstrate how to use the TCP utilities provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     Create a TCP server with custom handler
 *   </li>
 *   <li>
 *     Create a TCP client to connect to a server
 *   </li>
 *   <li>
 *     Send and receive TCP messages
 *   </li>
 *   <li>
 *     Handle TCP exceptions and errors
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link TcpServer}: TCP server implementation
 *   </li>
 *   <li>
 *     {@link TcpClient}: TCP client implementation
 *   </li>
 *   <li>
 *     {@link IOKit}: Utility for I/O operations
 *   </li>
 * </ul>
 */
public class TcpSample {

    public static void main(String[] args) {
        demonstrateTcpServerWithClient();
    }

    /**
     * Demonstrates creating a TCP server and connecting to it with a client.
     */
    public static void demonstrateTcpServerWithClient() {
        System.out.println("=== TCP Server with Client Demonstration ===");

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

                    // Send response
                    String response = "Hello, TCP client!";
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
            // Create TCP client and connect to server
            TcpClient tcpClient = TcpClient.newBuilder()
                .connect(new java.net.InetSocketAddress("127.0.0.1", tcpServer.localAddress().getPort()));

            System.out.println("TCP client connected to server");

            // Send message to server
            String message = "Hello, TCP server!";
            IOKit.write(tcpClient.channel(), message);
            System.out.println("Sent message: " + message);

            // Read response from server
            Thread.sleep(1000); // Wait for server response
            String response = IOKit.availableString(tcpClient.channel());
            System.out.println("Received response: " + response);

            // Close client
            tcpClient.close();
            System.out.println("TCP client closed");

        } catch (Exception e) {
            System.err.println("TCP operation failed: " + e.getMessage());
        } finally {
            // Close TCP server
            tcpServer.close();
            System.out.println("TCP server closed");
        }
    }
}