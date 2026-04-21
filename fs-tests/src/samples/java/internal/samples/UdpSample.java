package internal.samples;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.net.udp.UdpSender;
import space.sunqian.fs.net.udp.UdpServer;
import space.sunqian.fs.net.udp.UdpServerHandler;

/**
 * Sample: UDP Usage
 * <p>
 * Purpose: Demonstrate how to use the UDP utilities provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     Create a UDP server with custom handler
 *   </li>
 *   <li>
 *     Send UDP messages to a server
 *   </li>
 *   <li>
 *     Handle UDP exceptions and errors
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link UdpServer}: UDP server implementation
 *   </li>
 *   <li>
 *     {@link UdpSender}: UDP message sender
 *   </li>
 *   <li>
 *     {@link IOKit}: Utility for I/O operations
 *   </li>
 * </ul>
 */
public class UdpSample {

    public static void main(String[] args) {
        demonstrateUdpServerWithSender();
    }

    /**
     * Demonstrates creating a UDP server and sending messages to it.
     */
    public static void demonstrateUdpServerWithSender() {
        System.out.println("=== UDP Server with Sender Demonstration ===");

        // Create and start UDP server
        UdpServer udpServer = UdpServer.newBuilder()
            .handler(new UdpServerHandler() {
                @Override
                public void channelRead(@Nonnull java.nio.channels.DatagramChannel channel, @Nonnull byte[] data, @Nonnull java.net.SocketAddress address) throws Exception {
                    // Read message from client
                    String msg = new String(data);
                    System.out.println("Received message from " + address + ": " + msg);

                    // Send response
                    String response = "Hello, UDP client!";
                    channel.send(java.nio.ByteBuffer.wrap(response.getBytes()), address);
                    System.out.println("Sent response: " + response);
                }

                @Override
                public void exceptionCaught(@Nullable java.nio.channels.DatagramChannel channel, @Nonnull Throwable cause) {
                    System.err.println("Exception caught: " + cause.getMessage());
                }
            })
            .bind();

        System.out.println("UDP server started on port: " + udpServer.localAddress().getPort());

        try {
            // Create UDP sender
            UdpSender udpSender = UdpSender.newSender();

            // Send UDP message
            String message = "Hello, UDP server!";
            udpSender.sendData(message.getBytes(), new java.net.InetSocketAddress("127.0.0.1", udpServer.localAddress().getPort()));
            System.out.println("Sent message: " + message);

            // Wait for response
            Thread.sleep(1000);

        } catch (Exception e) {
            System.err.println("UDP operation failed: " + e.getMessage());
        } finally {
            // Close UDP server
            udpServer.close();
            System.out.println("UDP server closed");
        }
    }
}